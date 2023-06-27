package de.hhu.propra.link.archtests;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.hhu.propra.link.LinkApplication;
import org.springframework.stereotype.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packagesOf = LinkApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class)
public class RepositoryArchTests {


    @ArchTest
    public ArchRule repositoryClassNames =
        classes()
            .that()
            .areAnnotatedWith(Repository.class)
            .or()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beAnnotatedWith(Repository.class)
            .andShould()
            .haveSimpleNameEndingWith("Repository");

    @ArchTest
    public ArchRule repositoryClassLocation =
        classes()
            .that()
            .areAnnotatedWith(Repository.class)
            .or()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .resideInAPackage("de.hhu.propra.link.repositories");
}
