package tao.dong.dataconjurer.common.api;

import jakarta.validation.constraints.Min;

import java.util.Locale;
import java.util.List;

public interface CategorizedStringValueProvider {

    /**
     * Fetch values
     * @param count - number of record to be fetched
     * @param qualifier - a qualifier for sub category, such as firstname
     * @param locale - desired locale. When serializing/deserializing Jackson using BCP 47 format, which is the output format of <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Locale.html#toString()">Locale.toString()</a>.
     * @return A list of string values
     */
    List<String> fetch(@Min(1) int count, String qualifier, Locale locale);

}
