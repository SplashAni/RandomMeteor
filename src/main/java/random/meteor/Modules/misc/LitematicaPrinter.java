package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.systems.modules.Module;
import random.meteor.Main;

import javax.print.*;

public class LitematicaPrinter extends Module {

    public LitematicaPrinter() {
        super(Main.MISC,"litematica-printer","prints stuff yk...");
    }

    @Override
    public void onActivate() {
        PrintService printer = PrintServiceLookup.lookupDefaultPrintService(); //stackoverflow bro :yum:

        if(printer == null){
            toggle();
            error("Printer not found toggling...");
        }

        DocPrintJob printJob = printer.createPrintJob();

        Doc doc = new SimpleDoc("Litematica".getBytes(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null); //printing
        try {
            printJob.print(doc, null);
        } catch (PrintException e) {
            throw new RuntimeException(e);
        }

        toggle();
        super.onActivate();
    }
}
