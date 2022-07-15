plugins{
    application
    id("common-build")
}

application{
    mainClass.set("org.cirdles.peakShapes_Tripoli.gui.PeakShapesGUI")
}


dependencies {
    implementation(project(":PeakCore"))
    implementation("com.github.cirdles:commons:bc38781605")
}