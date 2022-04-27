package wang.liangchen.matrix.framework.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.configuration2.XMLConfiguration;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionsManager;
import wang.liangchen.matrix.framework.data.util.DatabaseUtil;
import wang.liangchen.matrix.framework.springboot.context.ConfigurationContext;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final Configuration freemarkerConfig;

    @Inject
    public DomainGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
        this.freemarkerConfig.setDefaultEncoding("UTF-8");
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }


    public void build() {
        // 获取配置信息
        List<GeneratorProperties> generatorPropertiesList = resolveConfiguration();
        // 填充数据库信息
        generatorPropertiesList.forEach(generatorProperties -> {
            populateColumnMetas(generatorProperties);
            createDomain(generatorProperties);
        });
    }

    private void createDomain(GeneratorProperties generatorProperties) {
        try {
            Template template = freemarkerConfig.getTemplate("Entity.ftl");
            String pathName = new StringBuilder()
                    .append(generatorProperties.getOutput())
                    .append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("domain").toString();
            Path path = Paths.get(pathName);
            if(Files.notExists(path)){
                Files.createDirectories(path);
            }
            path = path.resolve(generatorProperties.getClassName() + ".java");
            if(Files.exists(path)){
                throw new MatrixInfoException("xxxxxxxxxxxxxxxxxx");
            }
            Files.createFile(path);
            template.process(generatorProperties, new FileWriter(path.toFile()));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GeneratorProperties> resolveConfiguration() {
        XMLConfiguration generatorXml = (XMLConfiguration) ConfigurationContext.INSTANCE.resolve(GENERATOR_CONFIG_FILE);
        Document document = generatorXml.getDocument();
        Element element = document.getDocumentElement();
        String output = element.getAttribute("output");
        if (StringUtil.INSTANCE.isBlank(output)) {
            output = new StringBuilder().append(Symbol.USER_DIR.getSymbol())
                    .append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("src")
                    .append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("main")
                    .append(Symbol.FILE_SEPARATOR.getSymbol())
                    .append("java").toString();
        }
        //TODO 处理路径

        String basePackage = element.getAttribute("base-package");

        NodeList nodes = document.getElementsByTagName("entity");
        int length = nodes.getLength();
        String tableName, className, subPackage, columnVersion, columnMarkDelete, columnMarkDeleteValue;
        GeneratorProperties generatorProperties;
        List<GeneratorProperties> generatorPropertiesList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            generatorProperties = new GeneratorProperties();
            generatorProperties.setOutput(output);
            generatorProperties.setBasePackage(basePackage);

            tableName = generatorXml.getString(String.format("entity(%d).table-name", i));
            generatorProperties.setTableName(tableName);
            className = generatorXml.getString(String.format("entity(%d).class-name", i));
            generatorProperties.setClassName(className);
            subPackage = generatorXml.getString(String.format("entity(%d).sub-package", i));
            generatorProperties.setSubPackage(String.format("%s.domain", subPackage));
            columnVersion = generatorXml.getString(String.format("entity(%d).column-version", i));
            generatorProperties.setColumnVersion(columnVersion);
            columnMarkDelete = generatorXml.getString(String.format("entity(%d).column-markdelete", i));
            generatorProperties.setColumnMarkDelete(columnMarkDelete);
            columnMarkDeleteValue = generatorXml.getString(String.format("entity(%d).column-markdelete[@value]", i));
            generatorProperties.setColumnMarkDeleteValue(columnMarkDeleteValue);
            boolean camelCase = generatorXml.getBoolean(String.format("entity(%d).camel-case", i), Boolean.FALSE);
            generatorProperties.setCamelCase(camelCase);
            generatorPropertiesList.add(generatorProperties);
        }
        return generatorPropertiesList;
    }

    private void populateColumnMetas(GeneratorProperties generatorProperties) {
        String tableName = generatorProperties.getTableName();
        ConnectionsManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);
                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, generatorProperties.isCamelCase(),
                        generatorProperties.getColumnVersion(), generatorProperties.getColumnMarkDelete(), generatorProperties.getColumnMarkDeleteValue(),
                        primaryKeyColumnNames, uniqueKeyColumnNames);
                generatorProperties.setColumnMetas(columnMetas);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
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
