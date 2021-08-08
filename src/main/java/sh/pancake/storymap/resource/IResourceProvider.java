/*
 * Created on Mon Sep 28 2020
 *
 * Copyright (c) storycraft. Licensed under the GNU General Public License v3.
 */

package sh.pancake.storymap.resource;

import java.io.File;

public interface IResourceProvider {

    public File provide(File directory, String targetVersion, boolean recache) throws Exception;

}
