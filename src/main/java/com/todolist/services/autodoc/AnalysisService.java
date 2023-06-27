package com.todolist.services.autodoc;

import com.todolist.component.AnalysisTable;
import com.todolist.dtos.autodoc.Request;
import com.todolist.entity.Task;
import com.todolist.services.github.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {
    private final IssueService issueService;
    private final AutoDocService autoDocService;

    @Autowired
    public AnalysisService(IssueService issueService, AutoDocService autoDocService) {
        this.issueService = issueService;
        this.autoDocService = autoDocService;
    }

    @Transactional
    public Map<String, List<Task>> getAnalysis(Request request) throws IOException {
        autoDocService.autoDoc(request);
        return issueService.getTaskPerIssueFilter(request);
    }
}
