package com.armendtahiraga.App.services;

import com.armendtahiraga.App.repository.MediaRepository;

public class MediaService {
    MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }
}
