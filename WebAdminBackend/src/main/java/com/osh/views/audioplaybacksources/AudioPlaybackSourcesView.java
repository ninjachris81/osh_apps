package com.osh.views.audioplaybacksources;

import com.osh.data.entity.AudioPlaybackSource;
import com.osh.data.service.AudioPlaybackSourceService;
import com.osh.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("AudioPlaybackSources")
@Route(value = "AudioPlaybackSources/:audioPlaybackSourceID?/:action?(edit)", layout = MainLayout.class)
public class AudioPlaybackSourcesView extends Div implements BeforeEnterObserver {

    private final String AUDIOPLAYBACKSOURCE_ID = "audioPlaybackSourceID";
    private final String AUDIOPLAYBACKSOURCE_EDIT_ROUTE_TEMPLATE = "AudioPlaybackSources/%s/edit";

    private final Grid<AudioPlaybackSource> grid = new Grid<>(AudioPlaybackSource.class, false);

    private TextField name;
    private TextField sourceUrl;
    private Upload imageIcon;
    private Image imageIconPreview;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<AudioPlaybackSource> binder;

    private AudioPlaybackSource audioPlaybackSource;

    private final AudioPlaybackSourceService audioPlaybackSourceService;

    public AudioPlaybackSourcesView(AudioPlaybackSourceService audioPlaybackSourceService) {
        this.audioPlaybackSourceService = audioPlaybackSourceService;
        addClassNames("audio-playback-sources-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("sourceUrl").setAutoWidth(true);
        LitRenderer<AudioPlaybackSource> imageIconRenderer = LitRenderer.<AudioPlaybackSource>of(
                "<span style='border-radius: 50%; overflow: hidden; display: flex; align-items: center; justify-content: center; width: 64px; height: 64px'><img style='max-width: 100%' src=${item.imageIcon} /></span>")
                .withProperty("imageIcon", item -> {
                    if (item != null && item.getImageIcon() != null) {
                        return "data:image;base64," + Base64.getEncoder().encodeToString(item.getImageIcon());
                    } else {
                        return "";
                    }
                });
        grid.addColumn(imageIconRenderer).setHeader("Image Icon").setWidth("96px").setFlexGrow(0);

        grid.setItems(query -> audioPlaybackSourceService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent()
                        .navigate(String.format(AUDIOPLAYBACKSOURCE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AudioPlaybackSourcesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(AudioPlaybackSource.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        attachImageUpload(imageIcon, imageIconPreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.audioPlaybackSource == null) {
                    this.audioPlaybackSource = new AudioPlaybackSource();
                }
                binder.writeBean(this.audioPlaybackSource);
                audioPlaybackSourceService.update(this.audioPlaybackSource);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AudioPlaybackSourcesView.class);
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
        Optional<String> audioPlaybackSourceId = event.getRouteParameters().get(AUDIOPLAYBACKSOURCE_ID)
                .map(String::valueOf);
        if (audioPlaybackSourceId.isPresent()) {
            Optional<AudioPlaybackSource> audioPlaybackSourceFromBackend = audioPlaybackSourceService
                    .get(audioPlaybackSourceId.get());
            if (audioPlaybackSourceFromBackend.isPresent()) {
                populateForm(audioPlaybackSourceFromBackend.get());
            } else {
                Notification.show(String.format("The requested audioPlaybackSource was not found, ID = %s",
                        audioPlaybackSourceId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AudioPlaybackSourcesView.class);
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
        name = new TextField("Name");
        sourceUrl = new TextField("Source Url");
        Label imageIconLabel = new Label("Image Icon");
        imageIconPreview = new Image();
        imageIconPreview.setWidth("100%");
        imageIcon = new Upload();
        imageIcon.getStyle().set("box-sizing", "border-box");
        imageIcon.getElement().appendChild(imageIconPreview.getElement());
        formLayout.add(name, sourceUrl, imageIconLabel, imageIcon);

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

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            uploadBuffer.reset();
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            StreamResource resource = new StreamResource(e.getFileName(),
                    () -> new ByteArrayInputStream(uploadBuffer.toByteArray()));
            preview.setSrc(resource);
            preview.setVisible(true);
            if (this.audioPlaybackSource == null) {
                this.audioPlaybackSource = new AudioPlaybackSource();
            }
            this.audioPlaybackSource.setImageIcon(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(AudioPlaybackSource value) {
        this.audioPlaybackSource = value;
        binder.readBean(this.audioPlaybackSource);
        this.imageIconPreview.setVisible(value != null);
        if (value == null || value.getImageIcon() == null) {
            this.imageIcon.clearFileList();
            this.imageIconPreview.setSrc("");
        } else {
            this.imageIconPreview
                    .setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getImageIcon()));
        }

    }
}
