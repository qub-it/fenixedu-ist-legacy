package pt.ist.fenixedu.teacher.dto;

import java.io.Serializable;

import org.fenixedu.academic.domain.ExecutionYear;

public class ExecutionYearIntervalBean extends ExecutionYearBean implements Serializable {

    private ExecutionYear finalExecutionYear;

    public ExecutionYearIntervalBean() {
        setFirstExecutionYear(ExecutionYear.readFirstExecutionYear());
        setFinalExecutionYear(ExecutionYear.readLastExecutionYear());
    }

    public ExecutionYearIntervalBean(ExecutionYear firstExecutionYear, ExecutionYear finalExecutionYear) {
        setFirstExecutionYear(firstExecutionYear);
        setFinalExecutionYear(finalExecutionYear);
    }

    public void setFirstExecutionYear(ExecutionYear executionYear) {
        this.setExecutionYear(executionYear);
    }

    public ExecutionYear getFirstExecutionYear() {
        return this.getExecutionYear();
    }

    public void setFinalExecutionYear(ExecutionYear executionYear) {
        this.finalExecutionYear = executionYear;
    }

    public ExecutionYear getFinalExecutionYear() {
        return this.finalExecutionYear;
    }

    public ExecutionYear getFirstNonNullExecutionYear() {
        return getFirstExecutionYear() != null ? getFirstExecutionYear() : ExecutionYear.readFirstExecutionYear();
    }

    public ExecutionYear getFinalNonNullExecutionYear() {
        return getFinalExecutionYear() != null ? getFinalExecutionYear() : ExecutionYear.readLastExecutionYear();
    }
}
