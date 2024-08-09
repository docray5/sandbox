package com.falling.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.falling.events.Event;
import com.falling.events.Publisher;

public class Assets {
    private final AssetManager manager;
    private final Array<MyAssetDescriptor> assets;
    private boolean finished = false;

    public final Publisher publisher = new Publisher();

    public Assets() {
        manager = new AssetManager();
        manager.setLoader(TextureRegion[].class, new RegionArrLoader(new InternalFileHandleResolver()));
        manager.setLoader(ShaderLoader.Shader.class, new ShaderLoader(new InternalFileHandleResolver()));

        assets = new Array<>();

        assets.add(new MyAssetDescriptor("textures", Texture.class));
        assets.add(new MyAssetDescriptor("sounds", Sound.class));
        assets.add(new MyAssetDescriptor("fonts", BitmapFont.class));
        assets.add(new MyAssetDescriptor("sheets", TextureRegion[].class));
        assets.add(new MyAssetDescriptor("shaders", ShaderLoader.Shader.class));
    }

    public void load() {
        // Load loading screen:
        manager.load("textures/title.png", Texture.class);

        manager.finishLoading();

        // Load the rest:
        String assetFile = Gdx.files.internal("assets.txt").readString();
        String[] arr = assetFile.split("\n");

        for (MyAssetDescriptor descriptor : assets) {
            for (String asset : arr) {
                if (asset.split("\\.")[1].equals("txt")) continue;
                if (descriptor.folder.equals(asset.split("/")[0])) {
                    if (descriptor.assetType == TextureRegion[].class) {
                        String[] param = Gdx.files.internal(asset.split("\\.")[0] + ".txt").readString().split(",");

                        manager.load(new AssetDescriptor<>(asset, TextureRegion[].class,
                                new RegionArrLoader.RegionArrParameter(Integer.parseInt(param[0]), Integer.parseInt(param[1]), Integer.parseInt(param[2]))));
                    }
                    else manager.load(asset, descriptor.assetType);
                }
            }
        }
    }

    public void dispose() {
        manager.dispose();
    }

    public void update() {
        if (manager.update()) {
            loadPrefabs();
            publisher.notify(null, Event.LOADED_ASSETS);
            finished = true;
        }
    }

    private void loadPrefabs() {
    }

    public boolean isFinished() {
        return finished;
    }

    /**@param fileName no need to add "textures/" and ".png"*/
    public Texture getTexture(String fileName) {
        return manager.get("textures/" + fileName + ".png", Texture.class);
    }

    public Sound getSound(String fileName) {
        return manager.get("sounds/" + fileName, Sound.class);
    }

    public BitmapFont getFont(String fileName) {
        return manager.get("fonts/" + fileName, BitmapFont.class);
    }

    /**@param fileName no need to add "sheets/" and ".png"*/
    public TextureRegion[] getSheet(String fileName) {
        return manager.get("sheets/" + fileName + ".png", TextureRegion[].class);
    }

    /**@param fileName no need to add "shaders/" and ".glsl"*/
    public String getShader(String fileName) {
        return manager.get("shaders/" + fileName + ".glsl", ShaderLoader.Shader.class).getShader();
    }
}
