package engine;

import imgui.ImGui;
import imgui.ImFontAtlas;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiLayer {
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final ImGuiImplGl3 imGuiImplGl3;

    public ImGuiLayer() {
        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
    }

    public void init(long glfwWindow) {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);

        ImFontAtlas fonts = io.getFonts();
        fonts.addFontDefault();
        fonts.build();

        ImGui.styleColorsDark();
        ImGuiStyle style = ImGui.getStyle();
        style.setWindowRounding(0f);

        imGuiImplGl3.init("#version 330 core");
        imGuiImplGlfw.init(glfwWindow, true);
    }

    public void begin() {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public void end() {
        ImGui.endFrame();
    }

    public void destroy() {
        ImGui.destroyContext();
    }
}