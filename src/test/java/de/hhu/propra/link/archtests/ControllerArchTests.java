package de.hhu.propra.link.archtests;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.hhu.propra.link.LinkApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packagesOf = LinkApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class)
public class ControllerArchTests {

    @ArchTest
    static final ArchRule getMappingMethodsInControllersShouldReturnStringOrModelAndView =
        methods()
            .that()
            .areAnnotatedWith(GetMapping.class)
            .or()
            .areAnnotatedWith(PostMapping.class)
            .and()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(Controller.class)
            .should()
            .haveRawReturnType(String.class)
            .orShould()
            .haveRawReturnType(ModelAndView.class);
    @ArchTest
    public ArchRule controllerClassNames =
        classes()
            .that()
            .areAnnotatedWith(Controller.class)
            .or()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(Controller.class)
            .andShould()
            .haveSimpleNameEndingWith("Controller");

    @ArchTest
    public ArchRule controllerClassLocation =
        classes()
            .that()
            .areAnnotatedWith(Controller.class)
            .or()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .resideInAPackage("de.hhu.propra.link.controllers");
}
