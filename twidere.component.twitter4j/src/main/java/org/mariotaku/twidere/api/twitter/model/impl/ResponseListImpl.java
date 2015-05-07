/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.api.twitter.model.impl;

import org.mariotaku.simplerestapi.http.RestResponse;

import java.util.ArrayList;
import java.util.Collection;

import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.internal.util.InternalParseUtil;

/**
 * Created by mariotaku on 15/5/7.
 */
public class ResponseListImpl<T> extends ArrayList<T> implements ResponseList<T> {

    private int accessLevel;
    private RateLimitStatus rateLimitStatus;

    public ResponseListImpl(int capacity) {
        super(capacity);
    }

    public ResponseListImpl() {
    }

    public ResponseListImpl(Collection<? extends T> collection) {
        super(collection);
    }

    @Override
    public final void processResponseHeader(RestResponse resp) {
        rateLimitStatus = RateLimitStatusJSONImpl.createFromResponseHeader(resp);
        accessLevel = InternalParseUtil.toAccessLevel(resp);
    }

    @Override
    public final int getAccessLevel() {
        return accessLevel;
    }

    @Override
    public final RateLimitStatus getRateLimitStatus() {
        return rateLimitStatus;
    }
}
