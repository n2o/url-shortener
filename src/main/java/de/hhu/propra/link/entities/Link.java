package de.hhu.propra.link.entities;

import de.hhu.propra.link.util.StringUtil;
import de.hhu.propra.link.validation.UnreservedAbbreviation;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Data
@RedisHash("Link")
public class Link {

    public static final int MAX_ABBREVIATION_LENGTH = 32;

    @Id
    @Length(max = MAX_ABBREVIATION_LENGTH)
    @UnreservedAbbreviation
    private String abbreviation;

    @URL
    @NotEmpty
    private String url;

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = StringUtil.slugify(abbreviation);
    }
}
