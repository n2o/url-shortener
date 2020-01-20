package de.hhu.propra.link.entities;

import com.github.slugify.Slugify;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.NotEmpty;

@Data
@RedisHash("Link")
public class Link {

    public static final int MAX_ABBREVIATION_LENGTH = 32;

    @Id
    @Length(max = MAX_ABBREVIATION_LENGTH)
    private String abbreviation;

    @URL
    @NotEmpty
    private String url;

    public void setAbbreviation(String abbreviation) {
        var slg = new Slugify();
        this.abbreviation = slg.slugify(abbreviation);
    }
}
