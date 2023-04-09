package com.todolist.component;

import com.todolist.entity.Task;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return new StringBuilder()
                .append(new Heading(STATEMENTS, 3)).append(JUMP_LINE)
                .append(timeTasks.values().stream()
                        .map(tasks -> {
                            Task task = tasks.get(0);
                            return String.format("%s%s%s%s%s%n",
                                    LIST_ELEMENTS, new BoldText(task.getIdIssue()), SEPARATOR_ID, task.getTitleIssue(), JUMP_LINE);
                        })
                        .collect(Collectors.joining()));
    }

    public StringBuilder getAnalysis(Map<String, List<Task>> timeTasks) {
        return new StringBuilder()
                .append(new Heading(ANALYSIS, 3)).append(JUMP_LINE)
                .append(new Table.Builder()
                        .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                        .addRow(HEADER_ANALYSIS)
                        .withRows(timeTasks.values().stream()
                                .map(tasks -> {
                                    Task timeTask = tasks.get(0);
                                    String conclusion = Optional.ofNullable(timeTask.getConclusion()).orElse("").trim();
                                    String decision = Optional.ofNullable(timeTask.getDecision()).orElse("").trim();
                                    return new TableRow(List.of(timeTask.getIdIssue(), conclusion, decision));
                                }).toList())
                        .build().serialize());

    }
}
