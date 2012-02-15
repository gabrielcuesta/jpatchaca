package main;

import jira.service.JiraMock;
import main.singleInstance.AssureSingleInstance;
import model.PatchacaModelContainerFactory;

import org.picocontainer.MutablePicoContainer;

import ui.swing.singleInstance.ShowMainScreenOnSecondRun;
import ui.swing.tray.PatchacaTray;
import wheel.io.files.impl.tranzient.TransientDirectory;
import basic.DeferredExecutor;
import basic.HardwareClock;
import basic.PatchacaDirectory;

public final class SWINGContainerForTestsBuilder {

    public static MutablePicoContainer createSWINGContainerForTests(
            final HardwareClock hardwareClock) {

        DeferredExecutor.makeSynchronous();
        final MutablePicoContainer container = PatchacaModelContainerFactory
                .createNonUIContainer(hardwareClock);

        UIStuffBuilder.registerUIStuff(container);
        configureTestComponents(container);
        makePatchacaTrayStopShowingStatusMessages(container);
        return container;
    }

    private static void configureTestComponents(final MutablePicoContainer container) {
        container.removeComponent(PatchacaDirectory.class);
        container.addComponent(new TransientDirectory());

        container.removeComponent("JiraImpl");
        container.addComponent("JiraImpl", JiraMock.class);

        container.removeComponent(AssureSingleInstance.class);
        container.removeComponent(ShowMainScreenOnSecondRun.class);
    }

    private static void makePatchacaTrayStopShowingStatusMessages(
            final MutablePicoContainer container) {
        final PatchacaTray tray = container.getComponent(PatchacaTray.class);
        tray.test_mode = true;
    }
}