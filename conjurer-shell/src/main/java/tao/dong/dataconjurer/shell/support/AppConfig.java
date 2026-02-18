package tao.dong.dataconjurer.shell.support;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tao.dong.dataconjurer.common.service.CharacterGroupService;
import tao.dong.dataconjurer.common.service.DataGenerateService;
import tao.dong.dataconjurer.common.service.DataPlanService;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.service.DefaultDataProviderService;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DefaultAddressProvider;
import tao.dong.dataconjurer.common.support.DefaultEmailProvider;
import tao.dong.dataconjurer.common.support.DefaultIdProvider;
import tao.dong.dataconjurer.common.support.DefaultNameProvider;
import tao.dong.dataconjurer.engine.database.service.InsertStatementService;
import tao.dong.dataconjurer.engine.database.service.MySQLDataPlanService;
import tao.dong.dataconjurer.engine.database.service.MySQLInsertStatementService;
import tao.dong.dataconjurer.engine.database.service.SqlService;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Configuration
@SuppressWarnings("unused")
public class AppConfig {

    @Bean
    public YAMLMapper yamlMapper() {
        return YAMLMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
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
                .entityGenTimeOut(dataGenerationProperties.getEntityGenTimeOut())
                .maxIndexCollision(dataGenerationProperties.getMaxIndexCollision())
                .partialResult(dataGenerationProperties.isPartialResult())
                .dataGenCheckInterval(dataGenerationProperties.getDataGenCheckInterval())
                .dataGenTimeOut(dataGenerationProperties.getEntityGenTimeOut())
                .build();
    }

    @Bean
    public DataProviderService dataProviderService() {
        return new DefaultDataProviderService(new CharacterGroupService(), new DefaultEmailProvider(), new DefaultNameProvider(),
                new DefaultAddressProvider(), new DefaultIdProvider());
    }

    @Bean
    public DataPlanService dataPlanService(DataProviderService dataProviderService) {
        return new MySQLDataPlanService(dataProviderService);
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
