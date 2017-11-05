package de.qaware.qav.doc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO for Command documentation.
 *
 * @author QAware GmbH
 */
public class CommandDoc extends AbstractNameDescriptionDoc {

    private List<ParameterDoc> parameters = new ArrayList<>();

    private String result;

    public List<ParameterDoc> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDoc> parameters) {
        this.parameters.addAll(parameters);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CommandDoc that = (CommandDoc) o;
        return Objects.equals(parameters, that.parameters) &&
                Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameters, result);
    }

    @Override
    public String toString() {
        return "CommandDoc{" +
                super.toString() +
                ", parameters=" + parameters +
                ", result='" + result + '\'' +
                '}';
    }
}
