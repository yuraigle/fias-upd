package ru.irkoms.fias;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class FiasHandler extends DefaultHandler {
    private Boolean isRootElement = true;
    private List<String> props = new ArrayList<>();
    private List<String> propsDb = new ArrayList<>();
    private Connection conn;
    private PreparedStatement preparedInsert;
    private Integer cntInBatch = 0;
    private String type;
    private String region;

    public FiasHandler(String type, String region) {
        this.type = type;
        this.region = region;
        InputStream is = getClass().getResourceAsStream("/schema/" + type + ".txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        reader.lines().forEach(s -> {
            props.add(s.split("\\s+")[0]);
            propsDb.add(s);
        });
    }

    public List<String> getProps() {
        return props;
    }

    public List<String> getPropsDb() {
        return propsDb;
    }

    @Override
    public void startDocument() {
        try {
            conn = Main.connUp();
            String sql = "insert into " + type + " (" +
                    String.join(", ", props) + ") VALUES (" +
                    StringUtils.repeat("?,", props.size() - 1) + "?)";
            preparedInsert = conn.prepareStatement(sql);

            conn.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (isRootElement) {
            isRootElement = false;  // 1й элемент игнорируем
            return;
        }

        Map<String, String> obj = new HashMap<>();
        props.forEach(k -> obj.put(k, attributes.getValue(k)));

        if (isNotBlank(region) && props.contains("REGIONCODE")
                && attributes.getValue("REGIONCODE") != null && !attributes.getValue("REGIONCODE").equals(region))
            return;

        try {
            preparedInsert.clearParameters();
            for (int i = 0; i < props.size(); i++) {
                preparedInsert.setString(i + 1, obj.get(props.get(i)));
            }
            preparedInsert.addBatch();
            if (++cntInBatch % 10000 == 0) {
                preparedInsert.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endDocument() {
        try {
            preparedInsert.executeBatch();
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
