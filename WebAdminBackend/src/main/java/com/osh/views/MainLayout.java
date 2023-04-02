package com.osh.views;


import com.osh.components.appnav.AppNav;
import com.osh.components.appnav.AppNavItem;
import com.osh.views.audioplaybacksources.AudioPlaybackSourcesView;
import com.osh.views.dashboard.DashboardView;
import com.osh.views.knownareas.KnownAreasView;
import com.osh.views.knowndevices.KnownDevicesView;
import com.osh.views.knownrooms.KnownRoomsView;
import com.osh.views.processortasks.ProcessorTasksView;
import com.osh.views.processorvariables.ProcessorVariablesView;
import com.osh.views.valuegroups.ValueGroupsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("OSH Backend");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new AppNavItem("KnownAreas", KnownAreasView.class, LineAwesomeIcon.IMAGES.create()));
        nav.addItem(new AppNavItem("KnownRooms", KnownRoomsView.class, LineAwesomeIcon.DESKPRO.create()));
        nav.addItem(new AppNavItem("KnownDevices", KnownDevicesView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new AppNavItem("ValueGroups", ValueGroupsView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new AppNavItem("ProcessorTasks", ProcessorTasksView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new AppNavItem("ProcessorVariables", ProcessorVariablesView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new AppNavItem("AudioPlaybackSources", AudioPlaybackSourcesView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new AppNavItem("System Tree", SystemTreeView.class, LineAwesomeIcon.TREE_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
