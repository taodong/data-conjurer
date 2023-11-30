package tao.dong.dataconjurer.shell.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tao.dong.dataconjurer.common.api.CategorizedStringValueProvider;
import tao.dong.dataconjurer.common.api.CharacterGroupLookup;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.service.CharacterGroupService;
import tao.dong.dataconjurer.common.service.DataGenerateService;
import tao.dong.dataconjurer.common.service.DataPlanService;
import tao.dong.dataconjurer.common.service.DefaultDataProviderService;
import tao.dong.dataconjurer.common.support.DefaultAddressProvider;
import tao.dong.dataconjurer.common.support.DefaultEmailProvider;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DefaultNameProvider;
import tao.dong.dataconjurer.engine.database.service.InsertStatementService;
import tao.dong.dataconjurer.engine.database.service.MySQLDataPlanService;
import tao.dong.dataconjurer.engine.database.service.MySQLInsertStatementService;
import tao.dong.dataconjurer.engine.database.service.SqlService;

@Configuration
@SuppressWarnings("unused")
public class AppConfig {

    @Bean
    public ObjectMapper yamlMapper() {
        return JsonMapper.builder(new YAMLFactory())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .build();
    }

    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory()
                .getValidator();
    }

    @Bean
    public DataGenerateConfig dataGenerateConfig(DataGenerationProperties dataGenerationProperties) {
        return DataGenerateConfig.builder()
                .handlerCount(dataGenerationProperties.getHandlerCount())
                .entityGenTimeOut(dataGenerationProperties.getEntityGenTimeOut())
                .maxIndexCollision(dataGenerationProperties.getMaxIndexCollision())
                .partialResult(dataGenerationProperties.isPartialResult())
                .dataGenCheckInterval(dataGenerationProperties.getDataGenCheckInterval())
                .dataGenTimeOut(dataGenerationProperties.getEntityGenTimeOut())
                .build();
    }

    @Bean
    public CharacterGroupLookup characterGroupLookup() {
        return new CharacterGroupService();
    }


    @Bean
    public CategorizedStringValueProvider emailProvider() {
        return new DefaultEmailProvider();
    }

    @Bean
    public CategorizedStringValueProvider nameProvider() {
        return new DefaultNameProvider();
    }

    @Bean
    public CategorizedStringValueProvider addressProvider() {
        return new DefaultAddressProvider();
    }

    @Bean
    public V1DataProviderApi dataProviderApi(CharacterGroupLookup characterGroupLookup, CategorizedStringValueProvider emailProvider,
                                             CategorizedStringValueProvider nameProvider, CategorizedStringValueProvider addressProvider) {
        return DefaultDataProviderService.builder()
                .characterGroupLookup(characterGroupLookup)
                .emailProvider(emailProvider)
                .nameProvider(nameProvider)
                .addressProvider(addressProvider)
                .build();
    }

    @Bean
    public DataPlanService dataPlanService(V1DataProviderApi dataProviderApi) {
        return new MySQLDataPlanService(dataProviderApi);
    }

    @Bean
    public DataGenerateService dataGenerateService() {
        return new DataGenerateService();
    }

    @Bean
    public InsertStatementService insertStatementService() {
        return new MySQLInsertStatementService();
    }

    @Bean
    public SqlService sqlService(DataPlanService dataPlanService, DataGenerateService dataGenerateService, InsertStatementService insertStatementService) {
        return new SqlService(dataPlanService, dataGenerateService, insertStatementService);
    }
}
