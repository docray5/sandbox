package com.falling.assets;

public class MyAssetDescriptor {
    public String folder;
    public Class<?> assetType;

    public MyAssetDescriptor(String folder, Class<?> assetType) {
        this.folder = folder;
        this.assetType = assetType;
    }
}
