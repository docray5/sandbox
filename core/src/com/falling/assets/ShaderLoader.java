package com.falling.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class ShaderLoader extends AsynchronousAssetLoader<ShaderLoader.Shader, ShaderLoader.ShaderParameter> {
    private Shader shader;

    public ShaderLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager assetManager, String s, FileHandle fileHandle, ShaderParameter shaderParameter) {
        this.shader = null;
        this.shader = new Shader(new String(fileHandle.readBytes()));
    }

    @Override
    public Shader loadSync(AssetManager assetManager, String s, FileHandle fileHandle, ShaderParameter shaderParameter) {
        Shader shader = this.shader;
        this.shader = null;
        return shader;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String s, FileHandle fileHandle, ShaderParameter shaderParameter) {
        return null;
    }

    public static class ShaderParameter extends AssetLoaderParameters<Shader> {

    }

    public class Shader {
        private final String shader;

        public Shader(String s) {
            shader = s;
        }

        public String getShader() {
            return shader;
        }
    }
}
