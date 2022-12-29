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
import wang.liangchen.matrix.framework.commons.network.URIUtil;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.data.dao.StandaloneDao;
import wang.liangchen.matrix.framework.data.dao.criteria.ColumnMeta;
import wang.liangchen.matrix.framework.data.datasource.ConnectionManager;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.domain.EntityProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.message_pl.NorthboundMessagePlProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.controller.RemoteControllerProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.provider.RemoteProviderProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.resource.RemoteResourceProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.northbound_ohs.remote.subscriber.RemoteSubscriberProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.client.AdapterClientProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.publisher.AdapterPublisherProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.adapter.repository.AdapterRepositoryProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.message_pl.SouthboundMessagePlProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.client.PortClientProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.publisher.PortPublisherProperties;
import wang.liangchen.matrix.framework.generator.ddd.boundedcontext.southbound_acl.port.repository.PortRepositoryProperties;
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
public class DDDStyleGenerator {
    private final StandaloneDao standaloneDao;
    private final static String SQL = "select * from %s where 1=0";
    private final static String JAVA = ".java";
    private final static String GENERATOR_CONFIG_FILE = "ddd-generator.xml";
    private final Configuration freemarkerConfig;

    public DDDStyleGenerator(StandaloneDao standaloneDao) {
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
        // 填充数据库信息
        for (EntityProperties entityProperties : entitiesProperties) {
            createBoundedContext(entityProperties);
            createDomain(entityProperties);
            createAggregate(entityProperties);
            createEntity(entityProperties);
            createRepository(entityProperties);
            createClient(entityProperties);
            createPublisher(entityProperties);
            createSouthboundAclMessagePl(entityProperties);

            createController(entityProperties);
            createProvider(entityProperties);
            createResource(entityProperties);
            createSubscriber(entityProperties);
            createNorthboundOhsMessagePl(entityProperties);
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
        ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'package' attribute of the bounded context must not be blank");
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
                ValidationUtil.INSTANCE.notBlank(boundedContextName, "The 'table-name' attribute of the entity must not be blank");
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

    private void createBoundedContext(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getBoundedContextPackage(), "package-info.java", "BoundedContext.ftl", entityProperties);
    }

    private void createDomain(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getDomainPackage(), "package-info.java", "Domain.ftl", entityProperties);
    }

    private void createAggregate(EntityProperties entityProperties) {
        createFile(entityProperties.getOutput(), entityProperties.getAggregatePackage(), "package-info.java", "Aggregate.ftl", entityProperties);
    }

    private void createEntity(EntityProperties entityProperties) {
        // 填充数据元信息
        populateColumnMetas(entityProperties);
        createFile(entityProperties.getOutput(), entityProperties.getEntityPackage(), entityProperties.getEntityName().concat(".java"), "Entity.ftl", entityProperties);
    }

    private void createRepository(EntityProperties entityProperties) {
        PortRepositoryProperties portRepositoryProperties = new PortRepositoryProperties();
        // 填充南向网关
        portRepositoryProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        portRepositoryProperties.setAuthor(entityProperties.getAuthor());
        // 填充端口
        portRepositoryProperties.setPortPackage(portRepositoryProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("port"));
        // 填充仓库
        portRepositoryProperties.setPortRepositoryPackage(portRepositoryProperties.getPortPackage().concat(Symbol.DOT.getSymbol()).concat("repository"));
        portRepositoryProperties.setPortRepositoryName(StringUtil.INSTANCE.firstLetterUpperCase(entityProperties.getAggregateName()).concat("Repository"));
        createSouthboundAcl(entityProperties, portRepositoryProperties);
        createPort(entityProperties, portRepositoryProperties);
        createPortRepository(entityProperties, portRepositoryProperties);
        createPortRepositoryInterface(entityProperties, portRepositoryProperties);
        createAdapterRepository(entityProperties, portRepositoryProperties);
    }

    private void createClient(EntityProperties entityProperties) {
        PortClientProperties portClientProperties = new PortClientProperties();
        // 填充南向网关
        portClientProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        portClientProperties.setAuthor(entityProperties.getAuthor());
        // 填充端口
        portClientProperties.setPortPackage(portClientProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("port"));
        // 填充仓库
        portClientProperties.setPortClientPackage(portClientProperties.getPortPackage().concat(Symbol.DOT.getSymbol()).concat("client"));
        portClientProperties.setPortClientName(StringUtil.INSTANCE.firstLetterUpperCase(entityProperties.getAggregateName()).concat("Client"));

        createPortClient(entityProperties, portClientProperties);
        createPortClientInterface(entityProperties, portClientProperties);
        createAdapterClient(entityProperties, portClientProperties);
    }

    private void createPublisher(EntityProperties entityProperties) {
        PortPublisherProperties portPublisherProperties = new PortPublisherProperties();
        // 填充南向网关
        portPublisherProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        portPublisherProperties.setAuthor(entityProperties.getAuthor());
        // 填充端口
        portPublisherProperties.setPortPackage(portPublisherProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("port"));
        // 填充仓库
        portPublisherProperties.setPortPublisherPackage(portPublisherProperties.getPortPackage().concat(Symbol.DOT.getSymbol()).concat("publisher"));
        portPublisherProperties.setPortPublisherName(StringUtil.INSTANCE.firstLetterUpperCase(entityProperties.getAggregateName()).concat("Publisher"));

        createPortPublisher(entityProperties, portPublisherProperties);
        createPortPublisherInterface(entityProperties, portPublisherProperties);
        createAdapterPublisher(entityProperties, portPublisherProperties);
    }

    private void createSouthboundAclMessagePl(EntityProperties entityProperties) {
        SouthboundMessagePlProperties southboundMessagePlProperties = new SouthboundMessagePlProperties();
        // 填充南向网关
        southboundMessagePlProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        southboundMessagePlProperties.setAuthor(entityProperties.getAuthor());
        // 填充
        southboundMessagePlProperties.setMessagePlPackage(southboundMessagePlProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        createFile(entityProperties.getOutput(), southboundMessagePlProperties.getMessagePlPackage(), "package-info.java", "SouthboundMessagePl.ftl", southboundMessagePlProperties);
    }

    private void createAdapterRepository(EntityProperties entityProperties, PortRepositoryProperties portRepositoryProperties) {
        AdapterRepositoryProperties adapterRepositoryProperties = new AdapterRepositoryProperties();
        // 填充南向网关
        adapterRepositoryProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        adapterRepositoryProperties.setAuthor(entityProperties.getAuthor());
        // 填充端口
        adapterRepositoryProperties.setAdapterPackage(adapterRepositoryProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("adapter"));
        // 填充仓库
        adapterRepositoryProperties.setAdapterRepositoryPackage(adapterRepositoryProperties.getAdapterPackage().concat(Symbol.DOT.getSymbol()).concat("repository"));
        adapterRepositoryProperties.setAdapterRepositoryName(StringUtil.INSTANCE.firstLetterUpperCase(entityProperties.getAggregateName()).concat("RepositoryImpl"));
        adapterRepositoryProperties.setPortRepositoryPackage(portRepositoryProperties.getPortRepositoryPackage());
        adapterRepositoryProperties.setPortRepositoryName(portRepositoryProperties.getPortRepositoryName());

        createAdapter(entityProperties, adapterRepositoryProperties);
        createAdapterRepository(entityProperties, adapterRepositoryProperties);
        createAdapterRepositoryImpl(entityProperties, adapterRepositoryProperties);
    }

    private void createAdapterClient(EntityProperties entityProperties, PortClientProperties portClientProperties) {
        AdapterClientProperties adapterClientProperties = new AdapterClientProperties();
        // 填充南向网关
        adapterClientProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        adapterClientProperties.setAuthor(entityProperties.getAuthor());
        // 填充端口
        adapterClientProperties.setAdapterPackage(adapterClientProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("adapter"));
        // 填充仓库
        adapterClientProperties.setAdapterClientPackage(adapterClientProperties.getAdapterPackage().concat(Symbol.DOT.getSymbol()).concat("client"));
        adapterClientProperties.setAdapterClientName(StringUtil.INSTANCE.firstLetterUpperCase(entityProperties.getAggregateName()).concat("ClientImpl"));
        adapterClientProperties.setPortClientPackage(portClientProperties.getPortClientPackage());
        adapterClientProperties.setPortClientName(portClientProperties.getPortClientName());

        createAdapterClient(entityProperties, adapterClientProperties);
        createAdapterClientImpl(entityProperties, adapterClientProperties);
    }

    private void createAdapterPublisher(EntityProperties entityProperties, PortPublisherProperties portPublisherProperties) {
        AdapterPublisherProperties adapterPublisherProperties = new AdapterPublisherProperties();
        // 填充南向网关
        adapterPublisherProperties.setSouthboundAclPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("southbound_acl"));
        adapterPublisherProperties.setAuthor(entityProperties.getAuthor());
        // 填充端口
        adapterPublisherProperties.setAdapterPackage(adapterPublisherProperties.getSouthboundAclPackage().concat(Symbol.DOT.getSymbol()).concat("adapter"));
        // 填充仓库
        adapterPublisherProperties.setAdapterPublisherPackage(adapterPublisherProperties.getAdapterPackage().concat(Symbol.DOT.getSymbol()).concat("publisher"));
        adapterPublisherProperties.setAdapterPublisherName(StringUtil.INSTANCE.firstLetterUpperCase(entityProperties.getAggregateName()).concat("PublisherImpl"));
        adapterPublisherProperties.setPortPublisherPackage(portPublisherProperties.getPortPublisherPackage());
        adapterPublisherProperties.setPortPublisherName(portPublisherProperties.getPortPublisherName());

        createAdapterPublisher(entityProperties, adapterPublisherProperties);
        createAdapterPublisherImpl(entityProperties, adapterPublisherProperties);
    }

    private void createPortRepositoryInterface(EntityProperties entityProperties, PortRepositoryProperties portRepositoryProperties) {
        createFile(entityProperties.getOutput(), portRepositoryProperties.getPortRepositoryPackage(), portRepositoryProperties.getPortRepositoryName().concat(".java"), "PortRepositoryInterface.ftl", portRepositoryProperties);
    }

    private void createPortClientInterface(EntityProperties entityProperties, PortClientProperties portClientProperties) {
        createFile(entityProperties.getOutput(), portClientProperties.getPortClientPackage(), portClientProperties.getPortClientName().concat(".java"), "PortClientInterface.ftl", portClientProperties);
    }

    private void createPortPublisherInterface(EntityProperties entityProperties, PortPublisherProperties portPublisherProperties) {
        createFile(entityProperties.getOutput(), portPublisherProperties.getPortPublisherPackage(), portPublisherProperties.getPortPublisherName().concat(".java"), "PortPublisherInterface.ftl", portPublisherProperties);
    }

    private void createAdapterRepositoryImpl(EntityProperties entityProperties, AdapterRepositoryProperties adapterRepositoryProperties) {
        createFile(entityProperties.getOutput(), adapterRepositoryProperties.getAdapterRepositoryPackage(), adapterRepositoryProperties.getAdapterRepositoryName().concat(".java"), "AdapterRepositoryImpl.ftl", adapterRepositoryProperties);
    }

    private void createAdapterClientImpl(EntityProperties entityProperties, AdapterClientProperties adapterClientProperties) {
        createFile(entityProperties.getOutput(), adapterClientProperties.getAdapterClientPackage(), adapterClientProperties.getAdapterClientName().concat(".java"), "AdapterClientImpl.ftl", adapterClientProperties);
    }

    private void createAdapterPublisherImpl(EntityProperties entityProperties, AdapterPublisherProperties adapterPublisherProperties) {
        createFile(entityProperties.getOutput(), adapterPublisherProperties.getAdapterPublisherPackage(), adapterPublisherProperties.getAdapterPublisherName().concat(".java"), "AdapterPublisherImpl.ftl", adapterPublisherProperties);
    }


    private void createPortRepository(EntityProperties entityProperties, PortRepositoryProperties portRepositoryProperties) {
        createFile(entityProperties.getOutput(), portRepositoryProperties.getPortRepositoryPackage(), "package-info.java", "PortRepository.ftl", portRepositoryProperties);
    }

    private void createPortClient(EntityProperties entityProperties, PortClientProperties portClientProperties) {
        createFile(entityProperties.getOutput(), portClientProperties.getPortClientPackage(), "package-info.java", "PortClient.ftl", portClientProperties);
    }

    private void createPortPublisher(EntityProperties entityProperties, PortPublisherProperties portPublisherProperties) {
        createFile(entityProperties.getOutput(), portPublisherProperties.getPortPublisherPackage(), "package-info.java", "PortPublisher.ftl", portPublisherProperties);
    }

    private void createAdapterRepository(EntityProperties entityProperties, AdapterRepositoryProperties adapterRepositoryProperties) {
        createFile(entityProperties.getOutput(), adapterRepositoryProperties.getAdapterRepositoryPackage(), "package-info.java", "AdapterRepository.ftl", adapterRepositoryProperties);
    }

    private void createAdapterClient(EntityProperties entityProperties, AdapterClientProperties adapterClientProperties) {
        createFile(entityProperties.getOutput(), adapterClientProperties.getAdapterClientPackage(), "package-info.java", "AdapterClient.ftl", adapterClientProperties);
    }

    private void createAdapterPublisher(EntityProperties entityProperties, AdapterPublisherProperties adapterPublisherProperties) {
        createFile(entityProperties.getOutput(), adapterPublisherProperties.getAdapterPublisherPackage(), "package-info.java", "AdapterPublisher.ftl", adapterPublisherProperties);
    }


    private void createSouthboundAcl(EntityProperties entityProperties, PortRepositoryProperties portRepositoryProperties) {
        createFile(entityProperties.getOutput(), portRepositoryProperties.getSouthboundAclPackage(), "package-info.java", "SouthBoundAcl.ftl", portRepositoryProperties);
    }

    private void createPort(EntityProperties entityProperties, PortRepositoryProperties portRepositoryProperties) {
        createFile(entityProperties.getOutput(), portRepositoryProperties.getPortPackage(), "package-info.java", "Port.ftl", portRepositoryProperties);
    }

    private void createAdapter(EntityProperties entityProperties, AdapterRepositoryProperties adapterRepositoryProperties) {
        createFile(entityProperties.getOutput(), adapterRepositoryProperties.getAdapterPackage(), "package-info.java", "Adapter.ftl", adapterRepositoryProperties);
    }

    private void createController(EntityProperties entityProperties) {
        RemoteControllerProperties remoteControllerProperties = new RemoteControllerProperties();
        // 填充北向网关
        remoteControllerProperties.setNorthboundOhsPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_acl"));
        remoteControllerProperties.setAuthor(entityProperties.getAuthor());
        // 填充remote
        remoteControllerProperties.setRemotePackage(remoteControllerProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("remote"));
        // 填充controller
        remoteControllerProperties.setRemoteControllerPackage(remoteControllerProperties.getRemotePackage().concat(Symbol.DOT.getSymbol()).concat("controller"));

        createNorthboundOhs(entityProperties, remoteControllerProperties);
        createRemote(entityProperties, remoteControllerProperties);
        createRemoteController(entityProperties, remoteControllerProperties);
    }

    private void createNorthboundOhs(EntityProperties entityProperties, RemoteControllerProperties remoteControllerProperties) {
        createFile(entityProperties.getOutput(), remoteControllerProperties.getNorthboundOhsPackage(), "package-info.java", "NorthBoundOhs.ftl", remoteControllerProperties);
    }

    private void createRemote(EntityProperties entityProperties, RemoteControllerProperties remoteControllerProperties) {
        createFile(entityProperties.getOutput(), remoteControllerProperties.getRemotePackage(), "package-info.java", "Remote.ftl", remoteControllerProperties);
    }

    private void createRemoteController(EntityProperties entityProperties, RemoteControllerProperties remoteControllerProperties) {
        createFile(entityProperties.getOutput(), remoteControllerProperties.getRemoteControllerPackage(), "package-info.java", "RemoteController.ftl", remoteControllerProperties);
    }

    private void createProvider(EntityProperties entityProperties) {
        RemoteProviderProperties remoteProviderProperties = new RemoteProviderProperties();
        // 填充北向网关
        remoteProviderProperties.setNorthboundOhsPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_acl"));
        remoteProviderProperties.setAuthor(entityProperties.getAuthor());
        // 填充remote
        remoteProviderProperties.setRemotePackage(remoteProviderProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("remote"));
        // 填充Provider
        remoteProviderProperties.setRemoteProviderPackage(remoteProviderProperties.getRemotePackage().concat(Symbol.DOT.getSymbol()).concat("provider"));

        createRemoteProvider(entityProperties, remoteProviderProperties);
    }

    private void createRemoteProvider(EntityProperties entityProperties, RemoteProviderProperties remoteProviderProperties) {
        createFile(entityProperties.getOutput(), remoteProviderProperties.getRemoteProviderPackage(), "package-info.java", "RemoteProvider.ftl", remoteProviderProperties);
    }

    private void createResource(EntityProperties entityProperties) {
        RemoteResourceProperties remoteResourceProperties = new RemoteResourceProperties();
        // 填充北向网关
        remoteResourceProperties.setNorthboundOhsPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_acl"));
        remoteResourceProperties.setAuthor(entityProperties.getAuthor());
        // 填充remote
        remoteResourceProperties.setRemotePackage(remoteResourceProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("remote"));
        // 填充Resource
        remoteResourceProperties.setRemoteResourcePackage(remoteResourceProperties.getRemotePackage().concat(Symbol.DOT.getSymbol()).concat("resource"));

        createRemoteResource(entityProperties, remoteResourceProperties);
    }

    private void createRemoteResource(EntityProperties entityProperties, RemoteResourceProperties remoteResourceProperties) {
        createFile(entityProperties.getOutput(), remoteResourceProperties.getRemoteResourcePackage(), "package-info.java", "RemoteResource.ftl", remoteResourceProperties);
    }

    private void createSubscriber(EntityProperties entityProperties) {
        RemoteSubscriberProperties remoteSubscriberProperties = new RemoteSubscriberProperties();
        // 填充北向网关
        remoteSubscriberProperties.setNorthboundOhsPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_acl"));
        remoteSubscriberProperties.setAuthor(entityProperties.getAuthor());
        // 填充remote
        remoteSubscriberProperties.setRemotePackage(remoteSubscriberProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("remote"));
        // 填充Subscriber
        remoteSubscriberProperties.setRemoteSubscriberPackage(remoteSubscriberProperties.getRemotePackage().concat(Symbol.DOT.getSymbol()).concat("subscriber"));

        createRemoteSubscriber(entityProperties, remoteSubscriberProperties);
    }

    private void createRemoteSubscriber(EntityProperties entityProperties, RemoteSubscriberProperties remoteSubscriberProperties) {
        createFile(entityProperties.getOutput(), remoteSubscriberProperties.getRemoteSubscriberPackage(), "package-info.java", "RemoteSubscriber.ftl", remoteSubscriberProperties);
    }
    private void createNorthboundOhsMessagePl(EntityProperties entityProperties) {
        NorthboundMessagePlProperties northboundMessagePlProperties = new NorthboundMessagePlProperties();
        // 填充北向网关
        northboundMessagePlProperties.setNorthboundOhsPackage(entityProperties.getBoundedContextPackage().concat(Symbol.DOT.getSymbol()).concat("northbound_acl"));
        northboundMessagePlProperties.setAuthor(entityProperties.getAuthor());
        // 填充
        northboundMessagePlProperties.setMessagePlPackage(northboundMessagePlProperties.getNorthboundOhsPackage().concat(Symbol.DOT.getSymbol()).concat("message_pl"));
        createFile(entityProperties.getOutput(), northboundMessagePlProperties.getMessagePlPackage(), "package-info.java", "NorthboundMessagePl.ftl", northboundMessagePlProperties);
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

