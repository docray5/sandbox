package com.falling.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class RegionArrLoader extends AsynchronousAssetLoader<TextureRegion[], RegionArrLoader.RegionArrParameter> {
    private final RegionLoaderInfo info;
    private TextureRegion[][] tmp;
    private TextureRegion[] frames;

    public RegionArrLoader(FileHandleResolver resolver) {
        super(resolver);
        info = new RegionLoaderInfo();
        tmp = null;
        frames = null;
    }

    @Override
    public void loadAsync(AssetManager assetManager, String s, FileHandle fileHandle, RegionArrParameter parameter) {

        this.info.filename = s;
        if (parameter != null && parameter.textureData != null) {
            this.info.data = parameter.textureData;
            this.info.texture = parameter.texture;
        } else {
            Pixmap.Format format = null;
            boolean genMipMaps = false;
            this.info.texture = null;
            if (parameter != null) {
                format = parameter.format;
                genMipMaps = parameter.genMipMaps;
                this.info.texture = parameter.texture;
            }

            this.info.data = TextureData.Factory.loadFromFile(fileHandle, format, genMipMaps);
        }

        if (!this.info.data.isPrepared()) {
            this.info.data.prepare();
        }
    }

    @Override
    public TextureRegion[] loadSync(AssetManager assetManager, String s, FileHandle fileHandle, RegionArrParameter parameter) {
        if (this.info == null) {
            return null;
        } else {
            Texture texture = this.info.texture;
            if (texture != null) {
                texture.load(this.info.data);
            } else {
                texture = new Texture(this.info.data);
            }

            if (parameter != null) {
                texture.setFilter(parameter.minFilter, parameter.magFilter);
                texture.setWrap(parameter.wrapU, parameter.wrapV);
            }

            int totalFrames = parameter.cols*parameter.rows - parameter.emptyFrames;
            tmp = TextureRegion.split(texture,
                    texture.getWidth() / parameter.cols,
                    texture.getHeight() / parameter.rows);

            // Place the regions into a 1D array in the correct order, starting from the top
            // left, going across first. The Animation constructor requires a 1D array.
            frames = new TextureRegion[totalFrames];
            int index = 0;
            for (int i = 0; i < parameter.rows; i++) {
                for (int j = 0; j < parameter.cols; j++) {
                    if (index >= totalFrames) break;
                    frames[index++] = tmp[i][j];
                }
            }
            TextureRegion[] f = frames;
            tmp = null;
            frames = null;
            return f;
        }
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String s, FileHandle fileHandle, RegionArrParameter regionArrParameter) {
        return null;
    }

    public static class RegionArrParameter extends AssetLoaderParameters<TextureRegion[]> {
        public Pixmap.Format format = null;
        public boolean genMipMaps = false;
        public Texture texture = null;
        public TextureData textureData = null;
        public Texture.TextureFilter minFilter;
        public Texture.TextureFilter magFilter;
        public Texture.TextureWrap wrapU;
        public Texture.TextureWrap wrapV;
        public int cols;
        public int rows;
        public int emptyFrames;

        public RegionArrParameter(int cols, int rows, int emptyFrames) {
            this.cols = cols;
            this.rows = rows;
            this.emptyFrames = emptyFrames;
            this.minFilter = Texture.TextureFilter.Nearest;
            this.magFilter = Texture.TextureFilter.Nearest;
            this.wrapU = Texture.TextureWrap.ClampToEdge;
            this.wrapV = Texture.TextureWrap.ClampToEdge;
        }
    }

    public static class RegionLoaderInfo {
        String filename;
        TextureData data;
        Texture texture;

        public RegionLoaderInfo() {
        }
    }
}
