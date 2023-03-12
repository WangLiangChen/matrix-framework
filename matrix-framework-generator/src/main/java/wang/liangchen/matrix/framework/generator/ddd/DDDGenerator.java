package wang.liangchen.matrix.framework.generator.ddd;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.exception.MatrixInfoException;
import wang.liangchen.matrix.framework.commons.network.URIUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.ddd.northbound_ohs.remote.RemoteType;
import wang.liangchen.matrix.framework.ddd.southbound_acl.port.PortType;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.ManagerProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.NorthboundOhsProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.local.LocalCommandServiceProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.local.LocalProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.local.LocalQueryServiceProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl.*;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.RemoteProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.SouthboundAclProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.AdapterProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.message_pl.SouthboundMessagePlProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.PortProperties;
import wang.liangchen.matrix.framework.springboot.env.EnvironmentContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Liangchen.Wang 2022-12-28 18:31
 */
public class DDDGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "ddd-generator.xml";
    private final static String PACKAGE_INFO_FILE = "package-info.java";
    private final Configuration freemarkerConfig;

    public DDDGenerator(StandaloneDao standaloneDao) {
        this.standaloneDao = standaloneDao;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/ddd");
        String encoding = StandardCharsets.UTF_8.name();
        this.freemarkerConfig.setDefaultEncoding(encoding);
        this.freemarkerConfig.setOutputEncoding(encoding);
        this.freemarkerConfig.setEncoding(Locale.getDefault(), encoding);
        this.freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void generate() {
        // 获取配置
        List<EntityProperties> entitiesProperties = resolveConfiguration();
        // 校验聚合根唯一
        Set<String> aggregates = new HashSet<>();

        // 填充数据库信息
        for (EntityProperties entityProperties : entitiesProperties) {
            if (entityProperties.getAggregateRoot()) {
                String aggregateName = entityProperties.getAggregateName();
                if (aggregates.contains(aggregateName)) {
                    throw new MatrixInfoException("A aggregate has one aggregate only:{}", aggregateName);
                } else {
                    aggregates.add(aggregateName);
                }
            }

            createBoundedContextPackageInfo(entityProperties);
            createDomainPackageInfo(entityProperties);
            createAggregatePackageInfo(entityProperties);
            createEntity(entityProperties);

            SouthboundAclProperties southboundAclProperties = createSouthboundAclPackageInfo(entityProperties);
            createSouthboundAclMessagePlPackageInfo(entityProperties, southboundAclProperties);
            PortProperties portProperties = createPortPackageInfo(entityProperties, southboundAclProperties);
            List<PortProperties> portsProperties = createPort(entityProperties, portProperties);
            AdapterProperties adapterProperties = createAdapterPackageInfo(entityProperties, southboundAclProperties);
            createAdapter(entityProperties, adapterProperties, portsProperties);
            ManagerProperties managerProperties = createManager(entityProperties, portsProperties);

            NorthboundOhsProperties northboundOhsProperties = createNorthboundOhsPackageInfo(entityProperties);
            NorthboundMessagePlProperties northboundMessagePlProperties = createNorthboundOhsMessagePlPackageInfo(entityProperties, northboundOhsProperties);
            CommandRequestMessagePlProperties commandRequestMessagePlProperties = createCommandRequest(entityProperties, northboundMessagePlProperties);
            CommandResponseMessagePlProperties commandResponseMessagePlProperties = createCommandResponse(entityProperties, northboundMessagePlProperties);
            QueryRequestMessagePlProperties queryRequestMessagePlProperties = createQueryRequest(entityProperties, northboundMessagePlProperties);
            QueryResponseMessagePlProperties queryResponseMessagePlProperties = createQueryResponse(entityProperties, northboundMessagePlProperties);

            LocalProperties localProperties = createLocalPackageInfo(entityProperties, northboundOhsProperties);
            createLocalCommandService(entityProperties, localProperties, managerProperties, commandRequestMessagePlProperties, commandResponseMessagePlProperties);
            createLocalQueryService(entityProperties, localProperties, managerProperties, queryRequestMessagePlProperties, queryResponseMessagePlProperties);
            RemoteProperties remoteProperties = createRemotePackageInfo(entityProperties, northboundOhsProperties);
            createRemote(entityProperties, remoteProperties);
        }
    }


    private List<EntityProperties> resolveConfiguration() {
        String location = EnvironmentContext.INSTANCE.getURI(GENERATOR_CONFIG_FILE).toString();
        Document document;
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(location);
        } catch (Exception e) {
            throw new MatrixErrorException(e);
        }

        Element boundedContext = document.getDocumentElement();
        String boundedContextName = boundedContext.getAttribute("name");
        ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'name' attribute of the bounded context must not be blank");
        String basePackage = boundedContext.getAttribute("package");
        ValidationUtil.INSTANCE.notBlank(basePackage, "The 'package' attribute of the bounded context must not be blank");
        String datasource = boundedContext.getAttribute("datasource");
        String author = boundedContext.getAttribute("author");
        String output = boundedContext.getAttribute("output");
        if (StringUtil.INSTANCE.isBlank(output)) {
            try {
                output = ResourceUtils.getURL("").toString();
                output = output.concat("src").concat(Symbol.URI_SEPARATOR.getSymbol())
                        .concat("main").concat(Symbol.URI_SEPARATOR.getSymbol())
                        .concat("java").concat(Symbol.URI_SEPARATOR.getSymbol());
            } catch (FileNotFoundException e) {
                throw new MatrixErrorException(e);
            }
        }

        List<EntityProperties> entitiesProperties = new ArrayList<>();
        NodeList aggregates = boundedContext.getElementsByTagName("aggregate");
        for (int i = 0, j = aggregates.getLength(); i < j; i++) {
            Element aggregateElement = (Element) aggregates.item(i);
            String aggregateName = aggregateElement.getAttribute("name");
            ValidationUtil.INSTANCE.notBlank(aggregateName, "The 'name' attribute of the aggregate must not be blank");

            NodeList entities = aggregateElement.getElementsByTagName("entity");
            for (int k = 0, l = entities.getLength(); k < l; k++) {
                EntityProperties entityProperties = new EntityProperties();
                // 填充限界属性
                entityProperties.setBoundedContextName(boundedContextName);
                entityProperties.setBasePackage(basePackage);
                entityProperties.setBoundedContextPackage(basePackage.concat(Symbol.DOT.getSymbol()).concat(boundedContextName));
                entityProperties.setDatasource(datasource);
                entityProperties.setAuthor(author);
                entityProperties.setOutput(output);
                // 填充领域属性
                entityProperties.setDomainPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("domain"));
                // 填充聚合属性
                entityProperties.setAggregateName(aggregateName);
                entityProperties.setAggregatePackage(entityProperties.getDomainPackage().concat(Symbol.DOT.getSymbol()).concat(aggregateName));
                // 填充自身属性
                Element entityElement = (Element) entities.item(k);
                String tableName = entityElement.getAttribute("table-name");
                ValidationUtil.INSTANCE.notBlank(tableName, "The 'table-name' attribute of the entity must not be blank");
                entityProperties.setTableName(tableName);
                String entityName = entityElement.getAttribute("name");
                entityName = StringUtil.INSTANCE.isBlank(entityName) ? StringUtil.INSTANCE.underline2UpperCamelCase(tableName) : entityName;
                entityProperties.setEntityName(entityName);
                String aggregateRoot = entityElement.getAttribute("aggregate-root");
                Boolean isAggregateRoot = ObjectUtil.INSTANCE.castToBoolean(aggregateRoot, false);
                entityProperties.setAggregateRoot(isAggregateRoot);

                String columnVersion = null;
                NodeList nodes = entityElement.getElementsByTagName("column-version");
                Element element;
                if (nodes.getLength() == 1) {
                    element = (Element) nodes.item(0);
                    columnVersion = element.getTextContent();
                }
                entityProperties.setColumnVersion(columnVersion);
                String columnJson = null;
                nodes = entityElement.getElementsByTagName("column-json");
                if (nodes.getLength() == 1) {
                    element = (Element) nodes.item(0);
                    columnJson = element.getTextContent();
                }
                entityProperties.setColumnJson(columnJson);
                String columnState = null;
                Boolean isUseConstantEnum = null;
                nodes = entityElement.getElementsByTagName("column-state");
                if (nodes.getLength() == 1) {
                    element = (Element) nodes.item(0);
                    columnState = element.getTextContent();
                    String useConstantEnum = element.getAttribute("useConstantEnum");
                    isUseConstantEnum = ObjectUtil.INSTANCE.castToBoolean(useConstantEnum);
                }
                entityProperties.setColumnState(columnState);
                entityProperties.setColumnStateUseConstantEnum(isUseConstantEnum);

                String columnMarkDelete = null;
                String columnMarkDeleteValue = null;
                nodes = entityElement.getElementsByTagName("column-markdelete");
                if (nodes.getLength() == 1) {
                    element = (Element) nodes.item(0);
                    columnMarkDelete = element.getTextContent();
                    columnMarkDeleteValue = element.getAttribute("value");
                }
                entityProperties.setColumnMarkDelete(columnMarkDelete);
                entityProperties.setColumnMarkDeleteValue(columnMarkDeleteValue);
                entityProperties.setEntityPackage(entityProperties.getAggregatePackage());
                entitiesProperties.add(entityProperties);
            }
        }
        return entitiesProperties;
    }

    private void createBoundedContextPackageInfo(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getBoundedContextPackage(), PACKAGE_INFO_FILE, "BoundedContextPackageInfo.ftl", entityProperties);
    }

    private void createDomainPackageInfo(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getDomainPackage(), PACKAGE_INFO_FILE, "DomainPackageInfo.ftl", entityProperties);
    }

    private void createAggregatePackageInfo(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getAggregatePackage(), PACKAGE_INFO_FILE, "AggregatePackageInfo.ftl", entityProperties);
    }

    private void createEntity(EntityProperties entityProperties) {
        // 填充数据元信息
        populateColumnMetas(entityProperties);
        createFile(entityProperties.getOutput(), entityProperties.getEntityPackage(), entityProperties.getEntityName().concat(JAVA), "Entity.ftl", entityProperties);
    }

    private SouthboundAclProperties createSouthboundAclPackageInfo(EntityProperties entityProperties) {
        SouthboundAclProperties southboundAclProperties = new SouthboundAclProperties();
        southboundAclProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        southboundAclProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), southboundAclProperties.getSouthboundAclPackage(), PACKAGE_INFO_FILE, "SouthBoundAclPackageInfo.ftl", southboundAclProperties);
        return southboundAclProperties;
    }

    private SouthboundMessagePlProperties createSouthboundAclMessagePlPackageInfo(EntityProperties entityProperties, SouthboundAclProperties southboundAclProperties) {
        SouthboundMessagePlProperties southboundMessagePlProperties = new SouthboundMessagePlProperties();
        // 填充南向网关
        southboundMessagePlProperties.setSouthboundAclPackage(southboundAclProperties.getSouthboundAclPackage());
        southboundMessagePlProperties.setAuthor(entityProperties.getAuthor());
        // 填充
        southboundMessagePlProperties.setMessagePlPackage(southboundMessagePlProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        createFile(entityProperties.getOutput(), southboundMessagePlProperties.getMessagePlPackage(), PACKAGE_INFO_FILE, "SouthboundMessagePlPackageInfo.ftl", southboundMessagePlProperties);
        return southboundMessagePlProperties;
    }

    private PortProperties createPortPackageInfo(EntityProperties entityProperties, SouthboundAclProperties southboundAclProperties) {
        String portPackage = southboundAclProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("port");
        PortProperties portProperties = new PortProperties();
        portProperties.setPortPackage(portPackage);
        createFile(entityProperties.getOutput(), portProperties.getPortPackage(), PACKAGE_INFO_FILE, "PortPackageInfo.ftl", portProperties);
        return portProperties;
    }

    private List<PortProperties> createPort(EntityProperties entityProperties, PortProperties portProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return Collections.EMPTY_LIST;
        }
        List<PortProperties> portPropertiesList = new ArrayList<>();
        String portPackage = portProperties.getPortPackage();
        for (PortType portType : PortType.values()) {
            String portTypeName = portType.name();
            portProperties = new PortProperties();
            portProperties.setEntityProperties(entityProperties);
            portProperties.setAuthor(entityProperties.getAuthor());
            portProperties.setPortPackage(portPackage.concat(Symbol.DOT.getSymbol()).concat(portTypeName.toLowerCase()));
            portProperties.setPortType(portType);
            portProperties.setPortClassName(entityProperties.getEntityName().concat(portTypeName));
            createFile(entityProperties.getOutput(), portProperties.getPortPackage(), portProperties.getPortClassName().concat(JAVA), portTypeName.concat("PortInterface.ftl"), portProperties);
            portPropertiesList.add(portProperties);
        }
        return portPropertiesList;
    }

    private AdapterProperties createAdapterPackageInfo(EntityProperties entityProperties, SouthboundAclProperties southboundAclProperties) {
        String adapterPackage = southboundAclProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("adapter");
        AdapterProperties adapterProperties = new AdapterProperties();
        adapterProperties.setAdapterPackage(adapterPackage);
        createFile(entityProperties.getOutput(), adapterProperties.getAdapterPackage(), PACKAGE_INFO_FILE, "AdapterPackageInfo.ftl", adapterProperties);
        return adapterProperties;
    }

    private void createAdapter(EntityProperties entityProperties, AdapterProperties adapterProperties, List<PortProperties> portsProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return;
        }
        String adapterPackage = adapterProperties.getAdapterPackage();
        for (PortProperties portProperties : portsProperties) {
            PortType portType = portProperties.getPortType();
            String portTypeName = portType.name();
            adapterProperties = new AdapterProperties();
            adapterProperties.setEntityProperties(entityProperties);
            adapterProperties.setAuthor(entityProperties.getAuthor());
            adapterProperties.setPortPackage(portProperties.getPortPackage());
            adapterProperties.setPortClassName(portProperties.getPortClassName());
            adapterProperties.setAdapterPackage(adapterPackage.concat(Symbol.DOT.getSymbol()).concat(portTypeName.toLowerCase()));
            adapterProperties.setPortType(portType);
            adapterProperties.setAdapterClassName(entityProperties.getEntityName().concat(portTypeName).concat("Impl"));
            createFile(entityProperties.getOutput(), adapterProperties.getAdapterPackage(), adapterProperties.getAdapterClassName().concat(JAVA), portTypeName.concat("AdapterImpl.ftl"), adapterProperties);
        }
    }

    private ManagerProperties createManager(EntityProperties entityProperties, List<PortProperties> portsProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return null;
        }
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setEntityProperties(entityProperties);
        managerProperties.setAuthor(entityProperties.getAuthor());
        managerProperties.setManagerPackage(entityProperties.getEntityPackage());
        managerProperties.setManagerClassName(entityProperties.getEntityName().concat("Manager"));
        managerProperties.setPortsProperties(portsProperties);
        createFile(managerProperties.getEntityProperties().getOutput(), managerProperties.getManagerPackage(), managerProperties.getManagerClassName().concat(JAVA), "Manager.ftl", managerProperties);
        return managerProperties;
    }

    private NorthboundOhsProperties createNorthboundOhsPackageInfo(EntityProperties entityProperties) {
        NorthboundOhsProperties northboundOhsProperties = new NorthboundOhsProperties();
        northboundOhsProperties.setNorthboundOhsPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_ohs"));
        northboundOhsProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), northboundOhsProperties.getNorthboundOhsPackage(), PACKAGE_INFO_FILE, "NorthboundOhsPackageInfo.ftl", northboundOhsProperties);
        return northboundOhsProperties;
    }

    private NorthboundMessagePlProperties createNorthboundOhsMessagePlPackageInfo(EntityProperties entityProperties, NorthboundOhsProperties northboundOhsProperties) {
        NorthboundMessagePlProperties northboundMessagePlProperties = new NorthboundMessagePlProperties();
        // 填充北向网关
        northboundMessagePlProperties.setNorthboundOhsPackage(northboundOhsProperties.getNorthboundOhsPackage());
        northboundMessagePlProperties.setAuthor(entityProperties.getAuthor());
        // 填充
        northboundMessagePlProperties.setMessagePlPackage(northboundMessagePlProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        createFile(entityProperties.getOutput(), northboundMessagePlProperties.getMessagePlPackage(), PACKAGE_INFO_FILE, "NorthboundMessagePlPackageInfo.ftl", northboundMessagePlProperties);
        return northboundMessagePlProperties;
    }

    private CommandRequestMessagePlProperties createCommandRequest(EntityProperties entityProperties, NorthboundMessagePlProperties northboundMessagePlProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return null;
        }
        CommandRequestMessagePlProperties commandRequestMessagePlProperties = new CommandRequestMessagePlProperties();
        commandRequestMessagePlProperties.setCommandRequestPackage(northboundMessagePlProperties.getMessagePlPackage());
        commandRequestMessagePlProperties.setCommandRequestClassName(entityProperties.getEntityName().concat("CommandRequest"));
        commandRequestMessagePlProperties.setEntityProperties(entityProperties);
        commandRequestMessagePlProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), commandRequestMessagePlProperties.getCommandRequestPackage(), commandRequestMessagePlProperties.getCommandRequestClassName().concat(JAVA), "CommandRequest.ftl", commandRequestMessagePlProperties);
        return commandRequestMessagePlProperties;
    }

    private CommandResponseMessagePlProperties createCommandResponse(EntityProperties entityProperties, NorthboundMessagePlProperties northboundMessagePlProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return null;
        }
        CommandResponseMessagePlProperties commandResponseMessagePlProperties = new CommandResponseMessagePlProperties();
        commandResponseMessagePlProperties.setCommandResponsePackage(northboundMessagePlProperties.getMessagePlPackage());
        commandResponseMessagePlProperties.setCommandResponseClassName(entityProperties.getEntityName().concat("CommandResponse"));
        commandResponseMessagePlProperties.setEntityProperties(entityProperties);
        commandResponseMessagePlProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), commandResponseMessagePlProperties.getCommandResponsePackage(), commandResponseMessagePlProperties.getCommandResponseClassName().concat(JAVA), "CommandResponse.ftl", commandResponseMessagePlProperties);
        return commandResponseMessagePlProperties;
    }

    private QueryRequestMessagePlProperties createQueryRequest(EntityProperties entityProperties, NorthboundMessagePlProperties northboundMessagePlProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return null;
        }
        QueryRequestMessagePlProperties queryRequestMessagePlProperties = new QueryRequestMessagePlProperties();
        queryRequestMessagePlProperties.setQueryRequestPackage(northboundMessagePlProperties.getMessagePlPackage());
        queryRequestMessagePlProperties.setQueryRequestClassName(entityProperties.getEntityName().concat("QueryRequest"));
        queryRequestMessagePlProperties.setEntityProperties(entityProperties);
        queryRequestMessagePlProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), queryRequestMessagePlProperties.getQueryRequestPackage(), queryRequestMessagePlProperties.getQueryRequestClassName().concat(JAVA), "QueryRequest.ftl", queryRequestMessagePlProperties);
        return queryRequestMessagePlProperties;
    }

    private QueryResponseMessagePlProperties createQueryResponse(EntityProperties entityProperties, NorthboundMessagePlProperties northboundMessagePlProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return null;
        }
        QueryResponseMessagePlProperties queryResponseMessagePlProperties = new QueryResponseMessagePlProperties();
        queryResponseMessagePlProperties.setQueryResponsePackage(northboundMessagePlProperties.getMessagePlPackage());
        queryResponseMessagePlProperties.setQueryResponseClassName(entityProperties.getEntityName().concat("QueryResponse"));
        queryResponseMessagePlProperties.setEntityProperties(entityProperties);
        queryResponseMessagePlProperties.setAuthor(entityProperties.getAuthor());
        createFile(entityProperties.getOutput(), queryResponseMessagePlProperties.getQueryResponsePackage(), queryResponseMessagePlProperties.getQueryResponseClassName().concat(JAVA), "QueryResponse.ftl", queryResponseMessagePlProperties);
        return queryResponseMessagePlProperties;
    }

    private LocalProperties createLocalPackageInfo(EntityProperties entityProperties, NorthboundOhsProperties northboundOhsProperties) {
        String localPackage = northboundOhsProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("local");
        LocalProperties localProperties = new LocalProperties();
        localProperties.setLocalPackage(localPackage);
        createFile(entityProperties.getOutput(), localProperties.getLocalPackage(), PACKAGE_INFO_FILE, "LocalPackageInfo.ftl", localProperties);
        return localProperties;
    }

    private void createLocalCommandService(EntityProperties entityProperties, LocalProperties localProperties, ManagerProperties managerProperties, CommandRequestMessagePlProperties commandRequestMessagePlProperties, CommandResponseMessagePlProperties commandResponseMessagePlProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return;
        }
        LocalCommandServiceProperties localCommandServiceProperties = new LocalCommandServiceProperties();
        localCommandServiceProperties.setAuthor(entityProperties.getAuthor());
        localCommandServiceProperties.setCommandServicePackage(localProperties.getLocalPackage());
        localCommandServiceProperties.setCommandServiceClassName(entityProperties.getEntityName().concat("CommandService"));
        localCommandServiceProperties.setEntityProperties(entityProperties);
        localCommandServiceProperties.setManagerProperties(managerProperties);
        localCommandServiceProperties.setCommandRequestMessagePlProperties(commandRequestMessagePlProperties);
        localCommandServiceProperties.setCommandResponseMessagePlProperties(commandResponseMessagePlProperties);

        createFile(entityProperties.getOutput(), localCommandServiceProperties.getCommandServicePackage(), localCommandServiceProperties.getCommandServiceClassName().concat(JAVA), "LocalCommandService.ftl", localCommandServiceProperties);
    }

    private void createLocalQueryService(EntityProperties entityProperties, LocalProperties localProperties, ManagerProperties managerProperties, QueryRequestMessagePlProperties queryRequestMessagePlProperties, QueryResponseMessagePlProperties queryResponseMessagePlProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return;
        }
        LocalQueryServiceProperties localQueryServiceProperties = new LocalQueryServiceProperties();
        localQueryServiceProperties.setAuthor(entityProperties.getAuthor());
        localQueryServiceProperties.setQueryServicePackage(localProperties.getLocalPackage());
        localQueryServiceProperties.setQueryServiceClassName(entityProperties.getEntityName().concat("QueryService"));
        localQueryServiceProperties.setEntityProperties(entityProperties);
        localQueryServiceProperties.setManagerProperties(managerProperties);
        localQueryServiceProperties.setQueryRequestMessagePlProperties(queryRequestMessagePlProperties);
        localQueryServiceProperties.setQueryResponseMessagePlProperties(queryResponseMessagePlProperties);

        createFile(entityProperties.getOutput(), localQueryServiceProperties.getQueryServicePackage(), localQueryServiceProperties.getQueryServiceClassName().concat(JAVA), "LocalQueryService.ftl", localQueryServiceProperties);
    }

    private RemoteProperties createRemotePackageInfo(EntityProperties entityProperties, NorthboundOhsProperties northboundOhsProperties) {
        String remotePackage = northboundOhsProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("remote");
        RemoteProperties remoteProperties = new RemoteProperties();
        remoteProperties.setRemotePackage(remotePackage);
        createFile(entityProperties.getOutput(), remoteProperties.getRemotePackage(), PACKAGE_INFO_FILE, "RemotePackageInfo.ftl", remoteProperties);
        return remoteProperties;
    }

    private List<RemoteProperties> createRemote(EntityProperties entityProperties, RemoteProperties remoteProperties) {
        if (!entityProperties.getAggregateRoot()) {
            return Collections.EMPTY_LIST;
        }
        List<RemoteProperties> remotePropertiesList = new ArrayList<>();
        String remotePackage = remoteProperties.getRemotePackage();
        for (RemoteType remoteType : RemoteType.values()) {
            String remoteTypeName = remoteType.name();
            remoteProperties = new RemoteProperties();
            remoteProperties.setAuthor(entityProperties.getAuthor());
            remoteProperties.setRemotePackage(remotePackage.concat(Symbol.DOT.getSymbol()).concat(remoteTypeName.toLowerCase()));
            remoteProperties.setRemoteType(remoteType);
            remoteProperties.setRemoteClassName(entityProperties.getEntityName().concat(remoteTypeName));
            createFile(entityProperties.getOutput(), remoteProperties.getRemotePackage(), remoteProperties.getRemoteClassName().concat(JAVA), remoteTypeName.concat("Remote.ftl"), remoteProperties);
            remotePropertiesList.add(remoteProperties);
        }
        return remotePropertiesList;
    }

    private void createFile(String output, String packageName, String fileName, String templateName, Object dataModel) {
        URI uri = URIUtil.INSTANCE.toURI(output, StringUtil.INSTANCE.package2Path(packageName));
        Path path = Paths.get(uri);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(fileName);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Template template = freemarkerConfig.getTemplate(templateName);
                template.process(dataModel, new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8));
            }
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateColumnMetas(EntityProperties entityProperties) {
        String tableName = entityProperties.getTableName();
        ConnectionManager.INSTANCE.executeInNonManagedConnection((connection) -> {
            try {
                // 查询主键唯一键注释等信息
                DatabaseMetaData databaseMetaData = connection.getMetaData();
                ResultSet tables = databaseMetaData.getTables(null, null, tableName, new String[]{"TABLE"});
                while (tables.next()) {
                    String tableRemarks = tables.getString("REMARKS");
                    entityProperties.setTableComment(tableRemarks);
                }
                tables.close();
                ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);
                Map<String, String> columnCommentMap = new HashMap<>();
                while (columns.next()) {
                    columnCommentMap.put(columns.getString("COLUMN_NAME"), columns.getString("REMARKS"));
                }
                columns.close();

                List<String> primaryKeyColumnNames = primaryKeyColumnNames(databaseMetaData, tableName);
                List<String> uniqueKeyColumnNames = uniqueKeyColumnNames(databaseMetaData, tableName);
                uniqueKeyColumnNames.removeAll(primaryKeyColumnNames);

                List<ColumnMeta> columnMetas = resolveResultSetMetaData(connection, tableName, entityProperties, primaryKeyColumnNames, uniqueKeyColumnNames, columnCommentMap);
                entityProperties.getColumnMetas().addAll(columnMetas);
                entityProperties.getPkColumnMetas().addAll(columnMetas.stream().filter(ColumnMeta::isId).collect(Collectors.toList()));
                columnMetas.stream().filter(ColumnMeta::isState)
                        .findFirst().ifPresent(entityProperties::setStateColumnMeta);

                // 构造imports
                Set<String> imports = columnMetas.stream().map(ColumnMeta::getImportPackage).filter(StringUtil.INSTANCE::isNotBlank).collect(Collectors.toSet());
                entityProperties.getImports().addAll(imports);
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
        ResultSet resultSet = databaseMetaData.getIndexInfo(null, null, tableName, true, false);
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            uniqueKeyColumnNames.add(columnName);
        }
        resultSet.close();
        return uniqueKeyColumnNames;

    }

    private List<ColumnMeta> resolveResultSetMetaData(Connection connection, String tableName, EntityProperties entityProperties, List<String> primaryKeyColumnNames, List<String> uniqueKeyColumnNames, Map<String, String> columnCommentMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL, tableName));
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        String columnName, dataTypeName, jdbcTypeName;
        ColumnMeta columnMeta;
        List<ColumnMeta> columnMetas = new ArrayList<>();
        for (int i = 1, j = resultSetMetaData.getColumnCount(); i <= j; i++) {
            columnName = resultSetMetaData.getColumnName(i);
            dataTypeName = resultSetMetaData.getColumnTypeName(i);
            jdbcTypeName = resultSetMetaData.getColumnClassName(i);

            boolean isId = primaryKeyColumnNames.contains(columnName);
            boolean isUnique = uniqueKeyColumnNames.contains(columnName);
            boolean isVersion = columnName.equals(entityProperties.getColumnVersion());
            boolean isJson = columnName.equals(entityProperties.getColumnJson());
            boolean isState = columnName.equals(entityProperties.getColumnState());
            boolean isStateUseConstantEnum = entityProperties.isColumnStateUseConstantEnum();
            String _deleteValue = columnName.equals(entityProperties.getColumnMarkDelete()) ? entityProperties.getColumnMarkDeleteValue() : null;
            String columnComment = columnCommentMap.getOrDefault(columnName, "");
            columnComment = null == columnComment ? "" : columnComment;
            columnMeta = ColumnMeta.newInstance(columnName, dataTypeName, jdbcTypeName, isId, isUnique, isVersion, isJson, isState, isStateUseConstantEnum, _deleteValue, true, columnComment);
            columnMetas.add(columnMeta);
        }
        preparedStatement.close();
        resultSet.close();
        return columnMetas;
    }
}

