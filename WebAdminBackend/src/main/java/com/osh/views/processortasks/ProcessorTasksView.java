package com.osh.views.processortasks;

import com.osh.data.entity.ProcessorTask;
import com.osh.data.service.ProcessorTaskService;
import com.osh.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("ProcessorTasks")
@Route(value = "ProcessorTasks/:processorTaskID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
@Uses(Icon.class)
public class ProcessorTasksView extends Div implements BeforeEnterObserver {

    private final String PROCESSORTASK_ID = "processorTaskID";
    private final String PROCESSORTASK_EDIT_ROUTE_TEMPLATE = "ProcessorTasks/%s/edit";

    private final Grid<ProcessorTask> grid = new Grid<>(ProcessorTask.class, false);

    private TextField id;
    private TextField taskType;
    private TextField taskTriggerType;
    private TextArea scriptCode;
    private TextField runCondition;
    private TextField scheduleInterval;
    private Checkbox publishResult;
    private Checkbox enabled;
    private TextField groupId;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<ProcessorTask> binder;

    private ProcessorTask processorTask;

    private final ProcessorTaskService processorTaskService;

    public ProcessorTasksView(ProcessorTaskService processorTaskService) {
        this.processorTaskService = processorTaskService;
        addClassNames("processor-tasks-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true);
        grid.addColumn("taskType").setAutoWidth(true);
        grid.addColumn("taskTriggerType").setAutoWidth(true);
        grid.addColumn("scriptCode").setAutoWidth(true);
        grid.addColumn("runCondition").setAutoWidth(true);
        grid.addColumn("scheduleInterval").setAutoWidth(true);
        LitRenderer<ProcessorTask> publishResultRenderer = LitRenderer.<ProcessorTask>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", publishResult -> Boolean.TRUE.equals(publishResult.isPublishResult()) ? "check" : "minus")
                .withProperty("color",
                        publishResult -> Boolean.TRUE.equals(publishResult.isPublishResult())
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(publishResultRenderer).setHeader("Publish Result").setAutoWidth(true);

        LitRenderer<ProcessorTask> enabledRenderer = LitRenderer.<ProcessorTask>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", enabled -> enabled.isEnabled() ? "check" : "minus").withProperty("color",
                        enabled -> enabled.isEnabled()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(enabledRenderer).setHeader("Enabled").setAutoWidth(true);

        grid.addColumn("groupId").setAutoWidth(true);
        grid.setItems(query -> processorTaskService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PROCESSORTASK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ProcessorTasksView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ProcessorTask.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(taskType).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("taskType");
        binder.forField(taskTriggerType).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("taskTriggerType");
        binder.forField(scheduleInterval).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("scheduleInterval");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.processorTask == null) {
                    this.processorTask = new ProcessorTask();
                }
                binder.writeBean(this.processorTask);
                processorTaskService.update(this.processorTask);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ProcessorTasksView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> processorTaskId = event.getRouteParameters().get(PROCESSORTASK_ID).map(String::valueOf);
        if (processorTaskId.isPresent()) {
            Optional<ProcessorTask> processorTaskFromBackend = processorTaskService.get(processorTaskId.get());
            if (processorTaskFromBackend.isPresent()) {
                populateForm(processorTaskFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested processorTask was not found, ID = %s", processorTaskId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ProcessorTasksView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        id = new TextField("Id");
        taskType = new TextField("Task Type");
        taskTriggerType = new TextField("Task Trigger Type");
        scriptCode = new TextArea("Script Code");
        runCondition = new TextField("Run Condition");
        scheduleInterval = new TextField("Schedule Interval");
        publishResult = new Checkbox("Publish Result");
        enabled = new Checkbox("Enabled");
        groupId = new TextField("Group Id");
        formLayout.add(id, taskType, taskTriggerType, scriptCode, runCondition, scheduleInterval, publishResult, enabled,
                groupId);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(ProcessorTask value) {
        this.processorTask = value;
        binder.readBean(this.processorTask);

    }
}
