package wang.liangchen.matrix.framework.generator;

import freemarker.template.TemplateException;
import org.apache.commons.configuration2.XMLConfiguration;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionsManager;
import wang.liangchen.matrix.framework.data.util.DatabaseUtil;
import wang.liangchen.matrix.framework.springboot.context.ConfigurationContext;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-04-26 8:47
 */
@Component
public class DomainGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String GENERATOR_CONFIG_FILE = "generator.xml";

    @Inject
    public DomainGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
    }


    public void dododo() {
        // 获取配置信息
        GeneratorProperties generatorProperties = resolveConfiguration();
        // 获取数据库信息
    }

    private GeneratorProperties resolveConfiguration() {
        XMLConfiguration generatorXml = (XMLConfiguration) ConfigurationContext.INSTANCE.resolve(GENERATOR_CONFIG_FILE);
        Document document = generatorXml.getDocument();
        Element element = document.getDocumentElement();
        GeneratorProperties generatorProperties = new GeneratorProperties();
        String output = element.getAttribute("output");
        generatorProperties.setOutput(output);
        String basePackage = element.getAttribute("base-package");
        generatorProperties.setBasePackage(basePackage);
        NodeList nodes = document.getElementsByTagName("entity");
        int length = nodes.getLength();
        String table, className, subPackage, columnVersion, columnMarkDelete, columnMarkDeleteValue;
        GeneratorProperties.EntityProperties entityProperties;
        for (int i = 0; i < length; i++) {
            entityProperties = new GeneratorProperties.EntityProperties();
            table = generatorXml.getString(String.format("entity(%d).table", i));
            entityProperties.setTable(table);
            className = generatorXml.getString(String.format("entity(%d).class-name", i));
            entityProperties.setClassName(className);
            subPackage = generatorXml.getString(String.format("entity(%d).sub-package", i));
            entityProperties.setSubPackage(subPackage);
            columnVersion = generatorXml.getString(String.format("entity(%d).column-version", i));
            entityProperties.setColumnVersion(columnVersion);
            columnMarkDelete = generatorXml.getString(String.format("entity(%d).column-markdelete", i));
            entityProperties.setColumnMarkDelete(columnMarkDelete);
            columnMarkDeleteValue = generatorXml.getString(String.format("entity(%d).column-markdelete[@value]", i));
            entityProperties.setColumnMarkDeleteValue(columnMarkDeleteValue);
            boolean camelCase = generatorXml.getBoolean(String.format("entity(%d).camel-case", i), Boolean.FALSE);
            entityProperties.setCamelCase(camelCase);
            generatorProperties.getEntityProperties().put(className, entityProperties);
        }
        return generatorProperties;
    }

    public void doIt(String tableName, String versionColumn, String deleteColumn, String markDeleteValue) throws IOException, TemplateException {
       /* List<ColumnMeta> columnMetas = this.columnMetas(tableName, true, versionColumn, deleteColumn, markDeleteValue);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        //cfg.setClassForTemplateLoading(EntityTemplate.class,"template");
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Template template = cfg.getTemplate("Entity.ftl");
        EntityTemplate entityTemplate = new EntityTemplate();
        entityTemplate.setTableName(tableName);
        entityTemplate.setEntityName("AuthorizationSubject");
        entityTemplate.setBasePackage("com.sintrue.matrix.example");
        entityTemplate.setSubPackage("com.sintrue.matrix.example.domain");
        entityTemplate.setColumnMetas(columnMetas);
        template.process(entityTemplate, new OutputStreamWriter(System.out));*/
    }

    private List<ColumnMeta> columnMetas(GeneratorProperties.EntityProperties entityProperties) {
        String tableName = entityProperties.getTable();
        return ConnectionsManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);
                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, entityProperties.isCamelCase(),
                        entityProperties.getColumnVersion(), entityProperties.getColumnMarkDelete(), entityProperties.getColumnMarkDeleteValue(),
                        primaryKeyColumnNames, uniqueKeyColumnNames);
                return columnMetas;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<String> primaryKeyColumnNames(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        List<String> primaryKeyColumnNames = new ArrayList<>();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            primaryKeyColumnNames.add(columnName);
        }
        resultSet.close();
        return primaryKeyColumnNames;

    }

    private List<String> uniqueKeyColumnNames(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        List<String> uniqueKeyColumnNames = new ArrayList<>();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            uniqueKeyColumnNames.add(columnName);
        }
        resultSet.close();
        return uniqueKeyColumnNames;

    }

    private List<ColumnMeta> resolveResultSetMetaData(Connection connection, String tableName, boolean underline2camelCase, String versionColumn, String deleteColumn, String markDeleteValue, List<String> primaryKeyColumnNames, List<String> uniqueKeyColumnNames) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL, tableName));
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        String columnName, fieldName, jdbcTypeName, javaTypeName;
        ColumnMeta columnMeta;
        List<ColumnMeta> columnMetas = new ArrayList<>();
        for (int i = 1, j = resultSetMetaData.getColumnCount(); i <= j; i++) {
            columnName = resultSetMetaData.getColumnName(i);
            jdbcTypeName = resultSetMetaData.getColumnTypeName(i);
            javaTypeName = DatabaseUtil.INSTANCE.jdbcType2JavaType(jdbcTypeName);
            fieldName = columnName;
            if (underline2camelCase) {
                fieldName = StringUtil.INSTANCE.underline2camelCase(columnName);
            }
            boolean isId = primaryKeyColumnNames.contains(columnName);
            boolean isUnique = uniqueKeyColumnNames.contains(columnName);
            boolean isVersion = columnName.equals(versionColumn);
            String _deleteValue = columnName.equals(deleteColumn) ? markDeleteValue : null;

            columnMeta = ColumnMeta.newInstance(fieldName, javaTypeName, columnName, isId, isUnique, isVersion, _deleteValue);
            columnMetas.add(columnMeta);
        }
        preparedStatement.close();
        resultSet.close();
        return columnMetas;
    }
}
