package com.armendtahiraga.App.services;

import com.armendtahiraga.App.models.Media;
import com.armendtahiraga.App.repository.MediaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MediaService {
    MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<Media> getMedias() {
        try{
            Optional<List<Media>> medias = mediaRepository.getMedias();

            if (medias.isEmpty()){
                throw new IllegalArgumentException("No media found");
            }

            return medias.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during fetching medias");
        }
    }

    public Media createMedia(int creatorUserId, String title, String description, String mediaType, int releaseYear, int ageRestriction, List<String> genres) {
        try{
            Optional<Media> createdMedia = mediaRepository.createMedia(creatorUserId, title, description, mediaType, releaseYear, ageRestriction, genres);
            if (createdMedia.isEmpty()) {
                throw new IllegalArgumentException("Media could not be created");
            }

            return createdMedia.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during creating media");
        }
    }

    public Media updateMedia(int mediaId, String title, String description, String mediaType, int releaseYear, int ageRestriction, List<String> genres) {
        try{
            Optional<Media> updatedMedia = mediaRepository.updateMedia(mediaId, title, description, mediaType, releaseYear, ageRestriction, genres);
            if (updatedMedia.isEmpty()) {
                throw new IllegalArgumentException("Media could not be updated");
            }

            return updatedMedia.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during updating media");
        }
    }

    public Media getMediaById(int mediaId) {
        try{
            Optional<Media> media = mediaRepository.getMediaById(mediaId);
            if (media.isEmpty()) {
                throw new IllegalArgumentException("Media not found");
            }

            return media.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during fetching media by ID");
        }
    }

    public void deleteMedia(int mediaId) {
        try{
            mediaRepository.deleteMedia(mediaId);
        } catch (SQLException exception){
            throw new RuntimeException("DB error during deleting media");
        }
    }
}
