package de.hhu.propra.link.archtests;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.hhu.propra.link.LinkApplication;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packagesOf = LinkApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class)
public class ServiceArchTests {

    @ArchTest
    public ArchRule serviceClassNames =
        classes()
            .that()
            .areAnnotatedWith(Service.class)
            .or()
            .haveSimpleNameEndingWith("Service")
            .should()
            .beAnnotatedWith(Service.class)
            .andShould()
            .haveSimpleNameEndingWith("Service");

    @ArchTest
    public ArchRule serviceClassLocation =
        classes()
            .that()
            .areAnnotatedWith(Service.class)
            .or()
            .haveSimpleNameEndingWith("Service")
            .should()
            .resideInAPackage("de.hhu.propra.link.services");

}
