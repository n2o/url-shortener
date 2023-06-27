package de.hhu.propra.link.archtests;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import de.hhu.propra.link.LinkApplication;


@AnalyzeClasses(packagesOf = LinkApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureTests {

    @ArchTest
    public ArchRule layeredArchitectureForRepository =
        CompositeArchRule.of(
            classes()
                .that()
                .resideInAPackage("de.hhu.propra.link.repositories")
                .should(
                    not(
                        dependOnClassesThat(
                            resideInAPackage("de.hhu.propra.link.services"))))
                .andShould(
                    not(dependOnClassesThat(resideInAPackage("de.hhu.propra.link.controllers"))))
                .andShould(
                    onlyBeAccessedByClassesThat(resideInAPackage("de.hhu.propra.link")))
                .because("Persistence layer is the bottommost layer."));

    @ArchTest
    public ArchRule layeredArchitectureForDomain =
        CompositeArchRule.of(
            classes()
                .that()
                .resideInAPackage("de.hhu.propra.link.entities")
                .should(
                    not(
                        dependOnClassesThat(
                            resideInAPackage("de.hhu.propra.link.services"))))
                .andShould(
                    not(dependOnClassesThat(resideInAPackage("de.hhu.propra.link.controllers"))))
                .andShould(
                    onlyBeAccessedByClassesThat(
                        resideInAPackage("de.hhu.propra.link.entities")
                            .or(resideInAPackage("de.hhu.propra.link.services"))
                            .or(resideInAPackage("de.hhu.propra.link.controllers"))))
                .because("Domain layer is above persistence layer."));

    @ArchTest
    public ArchRule layeredArchitectureForServices =
        CompositeArchRule.of(
            classes()
                .that()
                .resideInAPackage("de.hhu.propra.link.services")
                .should(
                    not(dependOnClassesThat(resideInAPackage("de.hhu.propra.link.controller"))))
                .andShould(
                    onlyBeAccessedByClassesThat(
                        resideInAPackage("de.hhu.propra.link.services")
                            .or(resideInAPackage("de.hhu.propra.link.validation"))
                            .or(resideInAPackage("de.hhu.propra.link.controllers"))))
                .because("Service layer is above domain layer."));

    @ArchTest
    public ArchRule layeredArchitectureForController =
        CompositeArchRule.of(
            classes()
                .that()
                .resideInAPackage("de.hhu.propra.link.controllers")
                .should(
                    onlyBeAccessedByClassesThat(
                        resideInAPackage("de.hhu.propra.link.controllers")))
                .because("Controller layer is the topmost layer."));
}
