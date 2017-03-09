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
//TODO01: use ecdict as dictionary provider
//TODO02: make preference header layout beautiful more (TODO)
//TODO03: solve the different jdkName issue in app/app.iml
//TODO04: check if more rules are added to .gitignore
//TODO05: reduce timestamp to second unit (TODO)
//TODO06: handle network go out case (TODO)
//TODO07: get android simulator in T420
//TODO08: how to save dataset when application finish once. (join and foreground detection ?) (TODO)
//TODO09: a flow to reset all preferences (TODO)
//TODO10: utility page to transfer ecdict data to data.xml (TODO)
//TODO11: iLearn About page (TODO)
//TODO12: input the word from english studio classmate (one category)
//TODO13: common 7000 words
//TODO14: dickson idoms
//TODO15: new verifyStoragePermissions ok in SS 6.01 but NG in emulator 6.0
//TODO16: don't consider no voice case if checking if pron exam ok <=> dataset size (TODO)
//TODO17: test all data.xml for dictionary feasibility
//TODO18: score update by hash set flow => apply to checkWord (TODO))
//TODO19: enumerate words by category limitation
//TODO20: find default value from preference xml (TODO)
