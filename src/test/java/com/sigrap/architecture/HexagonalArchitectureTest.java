package com.sigrap.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * ArchUnit tests to validate hexagonal architecture implementation for the category module.
 * These tests ensure that architectural boundaries are respected and dependencies flow correctly.
 */
@DisplayName("Hexagonal Architecture Tests")
class HexagonalArchitectureTest {

    private static JavaClasses categoryClasses;

    @BeforeAll
    static void setUp() {
        categoryClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.sigrap.category");
    }

    @Nested
    @DisplayName("Layer Dependency Tests")
    class LayerDependencyTests {

        @Test
        @DisplayName("Domain layer should not depend on application layer")
        void domainLayerShouldNotDependOnApplicationLayer() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Domain layer should not depend on infrastructure layer")
        void domainLayerShouldNotDependOnInfrastructureLayer() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Application layer should not depend on infrastructure layer")
        void applicationLayerShouldNotDependOnInfrastructureLayer() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Application layer should only depend on domain layer")
        void applicationLayerShouldOnlyDependOnDomainLayer() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..application..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..application..",
                            "..domain..",
                            "java..",
                            "org.springframework.stereotype..",
                            "org.springframework.transaction.."
                    )
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Infrastructure layer can depend on application and domain layers")
        void infrastructureLayerCanDependOnApplicationAndDomainLayers() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..infrastructure..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..infrastructure..",
                            "..application..",
                            "..domain..",
                            "java..",
                            "jakarta..",
                            "org.springframework..",
                            "org.mapstruct..",
                            "com.fasterxml.jackson.."
                    )
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }
    }

    @Nested
    @DisplayName("Package Structure Tests")
    class PackageStructureTests {

        @Test
        @DisplayName("Domain models should reside in domain.model package")
        void domainModelsShouldResideInDomainModelPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameContaining("Category")
                    .and().resideInAPackage("..domain..")
                    .and().areNotInterfaces()
                    .should().resideInAPackage("..domain.model..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Repository ports should reside in domain.port package")
        void repositoryPortsShouldResideInDomainPortPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Port")
                    .and().resideInAPackage("..domain..")
                    .should().resideInAPackage("..domain.port..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Use case interfaces should reside in application.port.in package")
        void useCaseInterfacesShouldResideInApplicationPortInPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .and().areInterfaces()
                    .should().resideInAPackage("..application.port.in..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Use case implementations should reside in application.service package")
        void useCaseImplementationsShouldResideInApplicationServicePackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .and().resideInAPackage("..application..")
                    .should().resideInAPackage("..application.service..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Controllers should reside in infrastructure.adapter.in.rest package")
        void controllersShouldResideInInfrastructureAdapterInRestPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .and().resideInAPackage("..infrastructure..")
                    .should().resideInAPackage("..infrastructure.adapter.in.rest..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Persistence adapters should reside in infrastructure.adapter.out.persistence package")
        void persistenceAdaptersShouldResideInInfrastructureAdapterOutPersistencePackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Adapter")
                    .and().resideInAPackage("..infrastructure..")
                    .should().resideInAPackage("..infrastructure.adapter.out.persistence..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }
    }

    @Nested
    @DisplayName("Port and Adapter Pattern Tests")
    class PortAndAdapterPatternTests {

        @Test
        @DisplayName("Repository ports should be interfaces")
        void repositoryPortsShouldBeInterfaces() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Port")
                    .and().resideInAPackage("..domain.port..")
                    .should().beInterfaces()
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Adapters should implement ports")
        void adaptersShouldImplementPorts() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Adapter")
                    .and().resideInAPackage("..infrastructure.adapter.out..")
                    .should().implement(com.tngtech.archunit.base.DescribedPredicate.describe(
                            "interfaces from domain.port package",
                            javaClass -> javaClass.getAllRawInterfaces().stream()
                                    .anyMatch(iface -> iface.getPackageName().contains("domain.port"))
                    ))
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Controllers should depend on use case interfaces not implementations")
        void controllersShouldDependOnUseCaseInterfacesNotImplementations() {
            ArchRule rule = noClasses()
                    .that().haveSimpleNameEndingWith("Controller")
                    .and().resideInAPackage("..infrastructure..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application.service..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Use case interfaces should be in application.port.in package")
        void useCaseInterfacesShouldBeInApplicationPortInPackage() {
            ArchRule rule = classes()
                    .that().areInterfaces()
                    .and().haveSimpleNameEndingWith("UseCase")
                    .should().resideInAPackage("..application.port.in..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }
    }

    @Nested
    @DisplayName("Framework Dependency Tests")
    class FrameworkDependencyTests {

        @Test
        @DisplayName("Domain layer should not have Spring annotations")
        void domainLayerShouldNotHaveSpringAnnotations() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Domain layer should not have JPA annotations")
        void domainLayerShouldNotHaveJpaAnnotations() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Domain entities should be pure POJOs")
        void domainEntitiesShouldBePurePojos() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain.model..")
                    .and().haveSimpleNameContaining("Category")
                    .and().areNotInterfaces()
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..domain..",
                            "java.."
                    )
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("JPA entities should only be in infrastructure layer")
        void jpaEntitiesShouldOnlyBeInInfrastructureLayer() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("JpaEntity")
                    .should().resideInAPackage("..infrastructure.adapter.out.persistence..")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Spring annotations should only be in application and infrastructure layers")
        void springAnnotationsShouldOnlyBeInApplicationAndInfrastructureLayers() {
            ArchRule rule = classes()
                    .that().areAnnotatedWith("org.springframework.stereotype.Service")
                    .or().areAnnotatedWith("org.springframework.stereotype.Component")
                    .or().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                    .should().resideInAnyPackage(
                            "..application..",
                            "..infrastructure.."
                    )
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }
    }

    @Nested
    @DisplayName("Naming Convention Tests")
    class NamingConventionTests {

        @Test
        @DisplayName("Use case interfaces should end with 'UseCase'")
        void useCaseInterfacesShouldEndWithUseCase() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..application.port.in..")
                    .and().areInterfaces()
                    .and().areNotNestedClasses()
                    .and().areNotRecords()
                    .should().haveSimpleNameEndingWith("UseCase")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Port interfaces should end with 'Port'")
        void portInterfacesShouldEndWithPort() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain.port..")
                    .and().areInterfaces()
                    .should().haveSimpleNameEndingWith("Port")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Adapters should end with 'Adapter'")
        void adaptersShouldEndWithAdapter() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..infrastructure.adapter.out..")
                    .and().areNotInterfaces()
                    .and().haveSimpleNameNotEndingWith("Mapper")
                    .and().haveSimpleNameNotEndingWith("Entity")
                    .and().haveSimpleNameNotEndingWith("Repository")
                    .should().haveSimpleNameEndingWith("Adapter")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Controllers should end with 'Controller'")
        void controllersShouldEndWithController() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..infrastructure.adapter.in.rest..")
                    .and().areNotInterfaces()
                    .and().haveSimpleNameNotEndingWith("Mapper")
                    .and().haveSimpleNameNotEndingWith("Request")
                    .and().haveSimpleNameNotEndingWith("Response")
                    .and().areNotRecords()
                    .should().haveSimpleNameEndingWith("Controller")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }

        @Test
        @DisplayName("Use case implementations should end with 'Service'")
        void useCaseImplementationsShouldEndWithService() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..application.service..")
                    .and().areNotInterfaces()
                    .should().haveSimpleNameEndingWith("Service")
                    .allowEmptyShould(true);

            rule.check(categoryClasses);
        }
    }

    @Nested
    @DisplayName("Circular Dependency Tests")
    class CircularDependencyTests {

        @Test
        @DisplayName("No circular dependencies between packages")
        void noCircularDependenciesBetweenPackages() {
            slices()
                    .matching("com.sigrap.category.(*)..")
                    .should().beFreeOfCycles()
                    .allowEmptyShould(true)
                    .check(categoryClasses);
        }
    }
}
