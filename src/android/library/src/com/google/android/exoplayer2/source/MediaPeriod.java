/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import java.io.IOException;

/**
 * A source of a single period of media.
 */
public interface MediaPeriod extends SequenceableLoader {

  /**
   * A callback to be notified of {@link MediaPeriod} events.
   */
  interface Callback extends SequenceableLoader.Callback<MediaPeriod> {

    /**
     * Called when preparation completes.
     * <p>
     * May be called from any thread. After invoking this method, the {@link MediaPeriod} can expect
     * for {@link #selectTracks(TrackSelection[], boolean[], SampleStream[], boolean[], long)} to be
     * called with the initial track selection.
     *
     * @param mediaPeriod The prepared {@link MediaPeriod}.
     */
    void onPrepared(MediaPeriod mediaPeriod);

  }

  /**
   * Throws an error that's preventing the period from becoming prepared. Does nothing if no such
   * error exists.
   * <p>
   * This method should only be called before the period has completed preparation.
   *
   * @throws IOException The underlying error.
   */
  void maybeThrowPrepareError() throws IOException;

  /**
   * Returns the {@link TrackGroup}s exposed by the period.
   * <p>
   * This method should only be called after the period has been prepared.
   *
   * @return The {@link TrackGroup}s.
   */
  TrackGroupArray getTrackGroups();

  /**
   * Performs a track selection.
   * <p>
   * The call receives track {@code selections} for each renderer, {@code mayRetainStreamFlags}
   * indicating whether the existing {@code SampleStream} can be retained for each selection, and
   * the existing {@code stream}s themselves. The call will update {@code streams} to reflect the
   * provided selections, clearing, setting and replacing entries as required. If an existing sample
   * stream is retained but with the requirement that the consuming renderer be reset, then the
   * corresponding flag in {@code streamResetFlags} will be set to true. This flag will also be set
   * if a new sample stream is created.
   * <p>
   * This method should only be called after the period has been prepared.
   *
   * @param selections The renderer track selections.
   * @param mayRetainStreamFlags Flags indicating whether the existing sample stream can be retained
   *     for each selection. A {@code true} value indicates that the selection is unchanged, and
   *     that the caller does not require that the sample stream be recreated.
   * @param streams The existing sample streams, which will be updated to reflect the provided
   *     selections.
   * @param streamResetFlags Will be updated to indicate new sample streams, and sample streams that
   *     have been retained but with the requirement that the consuming renderer be reset.
   * @param positionUs The current playback position in microseconds.
   * @return The actual position at which the tracks were enabled, in microseconds.
   */
  long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags,
      SampleStream[] streams, boolean[] streamResetFlags, long positionUs);

  /**
   * Attempts to read a discontinuity.
   * <p>
   * After this method has returned a value other than {@link C#TIME_UNSET}, all
   * {@link SampleStream}s provided by the period are guaranteed to start from a key frame.
   *
   * @return If a discontinuity was read then the playback position in microseconds after the
   *     discontinuity. Else {@link C#TIME_UNSET}.
   */
  long readDiscontinuity();

  /**
   * Returns an estimate of the position up to which data is buffered for the enabled tracks.
   * <p>
   * This method should only be called when at least one track is selected.
   *
   * @return An estimate of the absolute position in microseconds up to which data is buffered, or
   *     {@link C#TIME_END_OF_SOURCE} if the track is fully buffered.
   */
  long getBufferedPositionUs();

  /**
   * Attempts to seek to the specified position in microseconds.
   * <p>
   * After this method has been called, all {@link SampleStream}s provided by the period are
   * guaranteed to start from a key frame.
   * <p>
   * This method should only be called when at least one track is selected.
   *
   * @param positionUs The seek position in microseconds.
   * @return The actual position to which the period was seeked, in microseconds.
   */
  long seekToUs(long positionUs);

}
