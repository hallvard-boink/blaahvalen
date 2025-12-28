package com.hallvardlaerum.basis;

import com.hallvardlaerum.libs.ui.Navigasjonsmenykyklop;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.annotation.UIScope;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Layout
@UIScope
public final class MainLayout extends AppLayout {

    MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()));
    }

    private Div createHeader() {
        // TODO Replace with real application logo and name
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.addClassNames(TextColor.PRIMARY, IconSize.LARGE);

        var appName = new Span("Blåhvalen");
        appName.addClassNames(FontWeight.SEMIBOLD, FontSize.LARGE);

        var header = new Div(appLogo, appName);
        header.addClassNames(Display.FLEX, Padding.MEDIUM, Gap.MEDIUM, AlignItems.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        SideNav sideNav = new SideNav();
        sideNav.addClassNames(Margin.Horizontal.MEDIUM);
        Navigasjonsmenykyklop.hent().leggMenyvalgTilRot(sideNav, "Poster","normalpost" );
        Navigasjonsmenykyklop.hent().leggMenyvalgTilRot(sideNav, "Månedsoversikter","maanedsoversikt");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilRot(sideNav, "Årsoversikter","aarsoversikt");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilRot(sideNav, "Budsjettposter","budsjettpost");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilRot(sideNav, "Verktøy","verktoy");

        SideNavItem detaljerSideNavItem = Navigasjonsmenykyklop.hent().leggMenyvalgforelderTilRot(sideNav, "Detaljer");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilForelder(detaljerSideNavItem, "Månedsoversiktposter","maanedsoversiktpost");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilForelder(detaljerSideNavItem, "Årsoversiktposter","aarsoversiktpost");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilForelder(detaljerSideNavItem, "Kostnadspakker","periodeoversiktpost");

        SideNavItem innstillingerSideNavItem = Navigasjonsmenykyklop.hent().leggMenyvalgforelderTilRot(sideNav, "Innstillinger");
        Navigasjonsmenykyklop.hent().leggMenyvalgTilForelder(innstillingerSideNavItem, "Kategorier","kategori");


        Navigasjonsmenykyklop.hent().leggMenyvalgTilRot(sideNav, "Om denne appen","");

        return sideNav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }
}
