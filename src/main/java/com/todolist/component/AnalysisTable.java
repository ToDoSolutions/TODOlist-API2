package com.todolist.component;

import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Task;
import com.todolist.services.autodoc.AnalysisService;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AnalysisTable {

    private static final String JUMP_LINE = "\n";
    private static final String SEPARATOR_ID = ": ";
    private static final String LIST_ELEMENTS = "- ";
    private static final Object[] HEADER_ANALYSIS = {"ID", "Conclusiones", "Decisiones tomadas"};
    private static final String ANALYSIS = "Análisis";
    private static final String STATEMENTS = "Enunciados";

    private final AnalysisService analysisService;

    @Autowired
    public AnalysisTable(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    public String createAnalysisTable(Request requestDto) throws IOException {
        Map<String, List<Task>> timeTasks = analysisService.getAnalysis(requestDto);

        return getStatements(timeTasks).append(JUMP_LINE)
                .append(getAnalysis(timeTasks))
                .toString();
    }


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
                                    return new TableRow<>(List.of(timeTask.getIdIssue(), conclusion, decision));
                                })
                                .collect(Collectors.toList()))
                        .build().serialize());
    }
}
