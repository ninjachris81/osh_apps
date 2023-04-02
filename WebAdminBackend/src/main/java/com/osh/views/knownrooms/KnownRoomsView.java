package com.osh.views.knownrooms;

import com.osh.data.entity.KnownArea;
import com.osh.data.entity.KnownRoom;
import com.osh.data.service.KnownAreaService;
import com.osh.data.service.KnownRoomService;
import com.osh.data.service.ServiceContext;
import com.osh.views.MainLayout;
import com.osh.views.ViewBase;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("KnownRooms")
@Route(value = "KnownRooms/:knownRoomID?/:action?(edit)", layout = MainLayout.class)
public class KnownRoomsView extends ViewBase implements BeforeEnterObserver {

    private final String KNOWNROOM_ID = "knownRoomID";
    private final String KNOWNROOM_EDIT_ROUTE_TEMPLATE = "KnownRooms/%s/edit";

    private final Grid<KnownRoom> grid = new Grid<>(KnownRoom.class, false);

    private TextField id;
    private TextField name;
    private ComboBox<KnownArea> knownArea;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<KnownRoom> binder;

    private KnownRoom knownRoom;

    private final KnownRoomService knownRoomService;

    public KnownRoomsView(ServiceContext serviceContext) {
        super(serviceContext);
        this.knownRoomService = serviceContext.getKnownRoomService();
        addClassNames("known-rooms-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(KnownRoom::getId).setAutoWidth(true);
        grid.addColumn(KnownRoom::getName).setAutoWidth(true);
        grid.addColumn(knownRoom1 -> knownRoom1.getKnownArea().getName());
        grid.setItems(query -> knownRoomService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(KNOWNROOM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(KnownRoomsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(KnownRoom.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.knownRoom == null) {
                    this.knownRoom = new KnownRoom();
                }
                binder.writeBean(this.knownRoom);
                knownRoomService.update(this.knownRoom);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(KnownRoomsView.class);
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
        Optional<String> knownRoomId = event.getRouteParameters().get(KNOWNROOM_ID).map(String::valueOf);
        if (knownRoomId.isPresent()) {
            Optional<KnownRoom> knownRoomFromBackend = knownRoomService.get(knownRoomId.get());
            if (knownRoomFromBackend.isPresent()) {
                populateForm(knownRoomFromBackend.get());
            } else {
                Notification.show(String.format("The requested knownRoom was not found, ID = %s", knownRoomId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(KnownRoomsView.class);
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
        name = new TextField("Name");
        knownArea = new ComboBox<>("Known Area");
        knownArea.setItemLabelGenerator(KnownArea::getName);
        knownArea.setItems(serviceContext.getKnownAreaService().list(Pageable.unpaged()).stream().toList());
        formLayout.add(id, name, knownArea);

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

    private void populateForm(KnownRoom value) {
        this.knownRoom = value;
        binder.readBean(this.knownRoom);

    }
}
