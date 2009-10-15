package com.goodworkalan.litany.mix;

import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.mix.BasicJavaModule;

public class LitanyModule extends BasicJavaModule {
    public LitanyModule() {
        super(new Artifact("com.goodworkalan", "litany", "0.7"));
        addTestDependency(new Artifact("org.testng", "testng", "5.10"));
    }
}
