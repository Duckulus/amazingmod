package eu.dhlserv.gabikmod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.8.8")
@IFMLLoadingPlugin.SortingIndex(1001)
public class FMLLoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
	return new String[] { ClassTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
	return null;
    }

    @Override
    public String getSetupClass() {
	return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
	return null;
    }

}
