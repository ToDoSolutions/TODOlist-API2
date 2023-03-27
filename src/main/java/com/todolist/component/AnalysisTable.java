package com.todolist.component;

import com.todolist.entity.Task;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AnalysisTable {

    // Constants --------------------------------------------------------------
    public static final String JUMP_LINE = "\n";
    public static final String SEPARATOR_ID = ": ";
    public static final String LIST_ELEMENTS = "- ";
    protected static final Object[] HEADER_ANALYSIS = {"ID", "Conclusiones", "Decisiones tomadas"};
    public static final String ANALYSIS = "Análisis";
    public static final String STATEMENTS = "Enunciados";


    // Methods ----------------------------------------------------------------
    public StringBuilder getStatements(Map<String, List<Task>> timeTasks) {
        StringBuilder output = new StringBuilder(new Heading(STATEMENTS, 3).toString()).append(JUMP_LINE);
        for (Map.Entry<String, List<Task>> entry : timeTasks.entrySet()) {
            Task task = entry.getValue().get(0);
            output.append(LIST_ELEMENTS).append(new BoldText(task.getIdIssue()))
                    .append(SEPARATOR_ID)
                    .append(task.getTitleIssue()).append(JUMP_LINE);
        }
        return output;
    }

    public StringBuilder getAnalysis(Map<String, List<Task>> timeTasks) {
        StringBuilder output = new StringBuilder(new Heading(ANALYSIS, 3).toString()).append(JUMP_LINE);
        Table.Builder table = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_ANALYSIS);
        for (Map.Entry<String, List<Task>> entry : timeTasks.entrySet()) {
            Task timeTask = entry.getValue().get(0);
            String conclusion = timeTask.getConclusion() != null ? timeTask.getConclusion().trim() : "";
            String decision = timeTask.getDecision() != null ? timeTask.getDecision().trim() : "";
            table.addRow(timeTask.getIdIssue(), conclusion, decision);
        }
        return output.append(table.build().serialize());

    }
}
