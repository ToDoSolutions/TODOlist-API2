package com.todolist.component;

import com.todolist.entity.autodoc.TimeTask;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalysisTable {

    public static final String JUMP_LINE = "\n";
    public static final String SEPARATOR_ID = ": ";
    public static final String LIST_ELEMENTS = "- ";
    public static final Object[] HEADER_ANALYSIS = {"ID", "Conclusiones", "Decisiones tomadas"};
    public static final String ANALYSIS = "Análisis";
    public static final String STATEMENTS = "Enunciados";

    public StringBuilder getStatements(List<TimeTask> timeTasks) {
        StringBuilder output = new StringBuilder(new Heading(STATEMENTS, 3).toString()).append(JUMP_LINE);
        for (TimeTask timeTask : timeTasks) {
            String[] text = timeTask.getTitle().split(SEPARATOR_ID);
            String id = text[0].trim();
            String body = text[1].trim();
            output.append(LIST_ELEMENTS).append(new BoldText(id)).append(SEPARATOR_ID).append(body).append(JUMP_LINE);
        }
        return output;
    }

    public StringBuilder getAnalysis(List<TimeTask> timeTasks) {
        StringBuilder output = new StringBuilder(new Heading(ANALYSIS, 3).toString()).append(JUMP_LINE);
        Table.Builder table = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                .addRow(HEADER_ANALYSIS);
        for (TimeTask timeTask : timeTasks) {
            String[] text = timeTask.getTitle().split(SEPARATOR_ID);
            String id = text[0].trim();
            table.addRow(id, timeTask.getConlusion().trim(), timeTask.getDecision().trim());
        }
        return output.append(table.build().serialize());

    }
}
