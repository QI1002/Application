package io.github.qi1002.ilearn;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}
//TODO00: how to do blocking wait in UI thread smartly.
//TODO01: link the global settings with real behaviors
//TODO02: make preference header layout beautiful more
//TODO03: solve the different jdkName issue in app/app.iml
//TODO04: check if more rules are added to .gitignore
//TODO05: reduce timestamp to second unit
//TODO06: handle network go out case
//TODO07: get android simulator in T420
//TODO08: how to save dataset when application finish once. (join and foreground detection ?)
//TODO09: a flow to reset all preferences
//TODO10: utility page to transfer ecdict data to data.xml
//TODO11: iLearn About page
//TODO12: wait voice play or dataset initialized options (debug)
//TODO13: common 7000 words
//TODO14: dickson idoms
//TODO15: move pronunciation answer to exam: ________
//TODO16: verifyStoragePermissions is enhanced ( not restart or auto restart APK again ?? )
//TODO17: don't consider no voice case if checking if pron exam ok <=> dataset size
//TODO18: test all data.xml for dictionary feasibility
//TODO19: review all final usage in *.java
//TODO20: score update by hash set flow => apply to checkWord
//TODO21: enumerate words by category limitation
//TODO22: find default value from preference xml
//TODO23: selection box with customer callback for positive/negative callback
//TODO24: use ecdict as dictionary provider
