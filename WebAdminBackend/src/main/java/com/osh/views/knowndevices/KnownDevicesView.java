package com.osh.views.knowndevices;

import com.osh.data.entity.KnownDevice;
import com.osh.data.service.KnownDeviceService;
import com.osh.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("KnownDevices")
@Route(value = "KnownDevices/:knownDeviceID?/:action?(edit)", layout = MainLayout.class)
public class KnownDevicesView extends Div implements BeforeEnterObserver {

    private final String KNOWNDEVICE_ID = "knownDeviceID";
    private final String KNOWNDEVICE_EDIT_ROUTE_TEMPLATE = "KnownDevices/%s/edit";

    private final Grid<KnownDevice> grid = new Grid<>(KnownDevice.class, false);

    private TextField id;
    private TextField serviceId;
    private TextField name;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<KnownDevice> binder;

    private KnownDevice knownDevice;

    private final KnownDeviceService knownDeviceService;

    public KnownDevicesView(KnownDeviceService knownDeviceService) {
        this.knownDeviceService = knownDeviceService;
        addClassNames("known-devices-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("id").setAutoWidth(true);
        grid.addColumn("serviceId").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
        grid.setItems(query -> knownDeviceService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(KNOWNDEVICE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(KnownDevicesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(KnownDevice.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.knownDevice == null) {
                    this.knownDevice = new KnownDevice();
                }
                binder.writeBean(this.knownDevice);
                knownDeviceService.update(this.knownDevice);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(KnownDevicesView.class);
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
        Optional<String> knownDeviceId = event.getRouteParameters().get(KNOWNDEVICE_ID).map(String::valueOf);
        if (knownDeviceId.isPresent()) {
            Optional<KnownDevice> knownDeviceFromBackend = knownDeviceService.get(knownDeviceId.get());
            if (knownDeviceFromBackend.isPresent()) {
                populateForm(knownDeviceFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested knownDevice was not found, ID = %s", knownDeviceId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(KnownDevicesView.class);
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
        serviceId = new TextField("Service Id");
        name = new TextField("Name");
        formLayout.add(id, serviceId, name);

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

    private void populateForm(KnownDevice value) {
        this.knownDevice = value;
        binder.readBean(this.knownDevice);

    }
}
