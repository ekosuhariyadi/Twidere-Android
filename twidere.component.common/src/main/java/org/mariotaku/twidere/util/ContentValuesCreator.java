/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.mariotaku.twidere.util;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.LoganSquare;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariotaku.twidere.TwidereConstants;
import org.mariotaku.twidere.api.twitter.auth.OAuthAuthorization;
import org.mariotaku.twidere.api.twitter.auth.OAuthToken;
import org.mariotaku.twidere.api.twitter.model.Activity;
import org.mariotaku.twidere.api.twitter.model.DirectMessage;
import org.mariotaku.twidere.api.twitter.model.Relationship;
import org.mariotaku.twidere.api.twitter.model.SavedSearch;
import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.api.twitter.model.Trend;
import org.mariotaku.twidere.api.twitter.model.Trends;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableAccount;
import org.mariotaku.twidere.model.ParcelableActivity;
import org.mariotaku.twidere.model.ParcelableActivityValuesCreator;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.twidere.model.ParcelableDirectMessage;
import org.mariotaku.twidere.model.ParcelableDirectMessageValuesCreator;
import org.mariotaku.twidere.model.ParcelableLocation;
import org.mariotaku.twidere.model.ParcelableMedia;
import org.mariotaku.twidere.model.ParcelableMediaUpdate;
import org.mariotaku.twidere.model.ParcelableStatus;
import org.mariotaku.twidere.model.ParcelableStatusUpdate;
import org.mariotaku.twidere.model.ParcelableStatusValuesCreator;
import org.mariotaku.twidere.model.ParcelableUser;
import org.mariotaku.twidere.model.ParcelableUserMention;
import org.mariotaku.twidere.model.ParcelableUserValuesCreator;
import org.mariotaku.twidere.provider.TwidereDataStore.Accounts;
import org.mariotaku.twidere.provider.TwidereDataStore.Activities;
import org.mariotaku.twidere.provider.TwidereDataStore.CachedRelationships;
import org.mariotaku.twidere.provider.TwidereDataStore.CachedTrends;
import org.mariotaku.twidere.provider.TwidereDataStore.CachedUsers;
import org.mariotaku.twidere.provider.TwidereDataStore.DirectMessages;
import org.mariotaku.twidere.provider.TwidereDataStore.Drafts;
import org.mariotaku.twidere.provider.TwidereDataStore.Filters;
import org.mariotaku.twidere.provider.TwidereDataStore.SavedSearches;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mariotaku.twidere.util.HtmlEscapeHelper.toPlainText;

public final class ContentValuesCreator implements TwidereConstants {

    public static ContentValues createAccount(final String basicUsername, final String basicPassword,
                                              final User user, final int color, final String apiUrlFormat,
                                              final boolean noVersionSuffix) {
        if (user == null || user.getId() <= 0) return null;
        final ContentValues values = new ContentValues();
        if (basicUsername == null || basicPassword == null) return null;
        values.put(Accounts.BASIC_AUTH_USERNAME, basicUsername);
        values.put(Accounts.BASIC_AUTH_PASSWORD, basicPassword);
        values.put(Accounts.AUTH_TYPE, ParcelableCredentials.AUTH_TYPE_BASIC);
        values.put(Accounts.ACCOUNT_ID, user.getId());
        values.put(Accounts.SCREEN_NAME, user.getScreenName());
        values.put(Accounts.NAME, user.getName());
        values.put(Accounts.PROFILE_IMAGE_URL, TwitterContentUtils.getProfileImageUrl(user));
        values.put(Accounts.PROFILE_BANNER_URL, user.getProfileBannerImageUrl());
        values.put(Accounts.COLOR, color);
        values.put(Accounts.IS_ACTIVATED, 1);
        values.put(Accounts.API_URL_FORMAT, apiUrlFormat);
        values.put(Accounts.NO_VERSION_SUFFIX, noVersionSuffix);
        return values;
    }

    public static ContentValues createAccount(final OAuthAuthorization auth, final User user,
                                              final int authType, final int color,
                                              final String apiUrlFormat, final boolean sameOAuthSigningUrl,
                                              final boolean noVersionSuffix) {
        if (user == null || auth == null) return null;
        final ContentValues values = new ContentValues();
        final OAuthToken accessToken = auth.getOauthToken();
        values.put(Accounts.OAUTH_TOKEN, accessToken.getOauthToken());
        values.put(Accounts.OAUTH_TOKEN_SECRET, accessToken.getOauthTokenSecret());
        values.put(Accounts.CONSUMER_KEY, auth.getConsumerKey());
        values.put(Accounts.CONSUMER_SECRET, auth.getConsumerSecret());
        values.put(Accounts.AUTH_TYPE, authType);
        values.put(Accounts.ACCOUNT_ID, user.getId());
        values.put(Accounts.SCREEN_NAME, user.getScreenName());
        values.put(Accounts.NAME, user.getName());
        values.put(Accounts.PROFILE_IMAGE_URL, TwitterContentUtils.getProfileImageUrl(user));
        values.put(Accounts.PROFILE_BANNER_URL, user.getProfileBannerImageUrl());
        values.put(Accounts.COLOR, color);
        values.put(Accounts.IS_ACTIVATED, 1);
        values.put(Accounts.API_URL_FORMAT, apiUrlFormat);
        values.put(Accounts.SAME_OAUTH_SIGNING_URL, sameOAuthSigningUrl);
        values.put(Accounts.NO_VERSION_SUFFIX, noVersionSuffix);
        return values;
    }

    public static ContentValues createAccount(final User user, final int color, final String apiUrlFormat,
                                              final boolean noVersionSuffix) {
        if (user == null || user.getId() <= 0) return null;
        final ContentValues values = new ContentValues();
        values.put(Accounts.AUTH_TYPE, ParcelableCredentials.AUTH_TYPE_TWIP_O_MODE);
        values.put(Accounts.ACCOUNT_ID, user.getId());
        values.put(Accounts.SCREEN_NAME, user.getScreenName());
        values.put(Accounts.NAME, user.getName());
        values.put(Accounts.PROFILE_IMAGE_URL, TwitterContentUtils.getProfileImageUrl(user));
        values.put(Accounts.PROFILE_BANNER_URL, user.getProfileBannerImageUrl());
        values.put(Accounts.COLOR, color);
        values.put(Accounts.IS_ACTIVATED, 1);
        values.put(Accounts.API_URL_FORMAT, apiUrlFormat);
        values.put(Accounts.NO_VERSION_SUFFIX, noVersionSuffix);
        return values;
    }

    public static ContentValues createCachedRelationship(final Relationship relationship,
                                                         final long accountId) {
        final ContentValues values = new ContentValues();
        values.put(CachedRelationships.ACCOUNT_ID, accountId);
        values.put(CachedRelationships.USER_ID, relationship.getTargetUserId());
        values.put(CachedRelationships.FOLLOWING, relationship.isSourceFollowingTarget());
        values.put(CachedRelationships.FOLLOWED_BY, relationship.isSourceFollowedByTarget());
        values.put(CachedRelationships.BLOCKING, relationship.isSourceBlockingTarget());
        values.put(CachedRelationships.BLOCKED_BY, relationship.isSourceBlockedByTarget());
        values.put(CachedRelationships.MUTING, relationship.isSourceMutingTarget());
        return values;
    }

    public static ContentValues createCachedUser(final User user) {
        if (user == null) return null;
        final ContentValues values = new ContentValues();
        ParcelableUserValuesCreator.writeTo(new ParcelableUser(user, -1), values);
        return values;
    }

    public static ContentValues createDirectMessage(final DirectMessage message, final long accountId,
                                                    final boolean isOutgoing) {
        if (message == null) return null;
        final ContentValues values = new ContentValues();
        final User sender = message.getSender(), recipient = message.getRecipient();
        if (sender == null || recipient == null) return null;
        final String sender_profile_image_url = TwitterContentUtils.getProfileImageUrl(sender);
        final String recipient_profile_image_url = TwitterContentUtils.getProfileImageUrl(recipient);
        values.put(DirectMessages.ACCOUNT_ID, accountId);
        values.put(DirectMessages.MESSAGE_ID, message.getId());
        values.put(DirectMessages.MESSAGE_TIMESTAMP, message.getCreatedAt().getTime());
        values.put(DirectMessages.SENDER_ID, sender.getId());
        values.put(DirectMessages.RECIPIENT_ID, recipient.getId());
        if (isOutgoing) {
            values.put(DirectMessages.CONVERSATION_ID, recipient.getId());
        } else {
            values.put(DirectMessages.CONVERSATION_ID, sender.getId());
        }
        final String text_html = TwitterContentUtils.formatDirectMessageText(message);
        values.put(DirectMessages.TEXT_HTML, text_html);
        values.put(DirectMessages.TEXT_PLAIN, message.getText());
        values.put(DirectMessages.TEXT_UNESCAPED, toPlainText(text_html));
        values.put(DirectMessages.IS_OUTGOING, isOutgoing);
        values.put(DirectMessages.SENDER_NAME, sender.getName());
        values.put(DirectMessages.SENDER_SCREEN_NAME, sender.getScreenName());
        values.put(DirectMessages.RECIPIENT_NAME, recipient.getName());
        values.put(DirectMessages.RECIPIENT_SCREEN_NAME, recipient.getScreenName());
        values.put(DirectMessages.SENDER_PROFILE_IMAGE_URL, sender_profile_image_url);
        values.put(DirectMessages.RECIPIENT_PROFILE_IMAGE_URL, recipient_profile_image_url);
        final ParcelableMedia[] mediaArray = ParcelableMedia.fromEntities(message);
        if (mediaArray != null) {
            try {
                values.put(DirectMessages.MEDIA_JSON, LoganSquare.serialize(Arrays.asList(mediaArray), ParcelableMedia.class));
            } catch (IOException ignored) {
            }
        }
        return values;
    }

    public static ContentValues createDirectMessage(final ParcelableDirectMessage message) {
        if (message == null) return null;
        final ContentValues values = new ContentValues();
        ParcelableDirectMessageValuesCreator.writeTo(message, values);
        return values;
    }

    public static ContentValues createFilteredUser(final ParcelableStatus status) {
        if (status == null) return null;
        final ContentValues values = new ContentValues();
        values.put(Filters.Users.USER_ID, status.user_id);
        values.put(Filters.Users.NAME, status.user_name);
        values.put(Filters.Users.SCREEN_NAME, status.user_screen_name);
        return values;
    }

    public static ContentValues createFilteredUser(final ParcelableUser user) {
        if (user == null) return null;
        final ContentValues values = new ContentValues();
        values.put(Filters.Users.USER_ID, user.id);
        values.put(Filters.Users.NAME, user.name);
        values.put(Filters.Users.SCREEN_NAME, user.screen_name);
        return values;
    }

    public static ContentValues createFilteredUser(final ParcelableUserMention user) {
        if (user == null) return null;
        final ContentValues values = new ContentValues();
        values.put(Filters.Users.USER_ID, user.id);
        values.put(Filters.Users.NAME, user.name);
        values.put(Filters.Users.SCREEN_NAME, user.screen_name);
        return values;
    }

    public static ContentValues createMessageDraft(final long accountId, final long recipientId,
                                                   final String text, final String imageUri) {
        final ContentValues values = new ContentValues();
        values.put(Drafts.ACTION_TYPE, Drafts.ACTION_SEND_DIRECT_MESSAGE);
        values.put(Drafts.TEXT, text);
        values.put(Drafts.ACCOUNT_IDS, TwidereArrayUtils.toString(new long[]{accountId}, ',', false));
        values.put(Drafts.TIMESTAMP, System.currentTimeMillis());
        if (imageUri != null) {
            final ParcelableMediaUpdate[] mediaArray = {new ParcelableMediaUpdate(imageUri, 0)};
            try {
                values.put(Drafts.MEDIA, LoganSquare.serialize(Arrays.asList(mediaArray), ParcelableMediaUpdate.class));
            } catch (IOException ignored) {
            }
        }
        final JSONObject extras = new JSONObject();
        try {
            extras.put(EXTRA_RECIPIENT_ID, recipientId);
        } catch (final JSONException e) {
            e.printStackTrace();
        }
        values.put(Drafts.ACTION_EXTRAS, extras.toString());
        return values;
    }

    public static ContentValues createSavedSearch(final SavedSearch savedSearch, final long accountId) {
        final ContentValues values = new ContentValues();
        values.put(SavedSearches.ACCOUNT_ID, accountId);
        values.put(SavedSearches.SEARCH_ID, savedSearch.getId());
        values.put(SavedSearches.CREATED_AT, savedSearch.getCreatedAt().getTime());
        values.put(SavedSearches.NAME, savedSearch.getName());
        values.put(SavedSearches.QUERY, savedSearch.getQuery());
        return values;
    }

    public static ContentValues[] createSavedSearches(final List<SavedSearch> savedSearches, long accountId) {
        final ContentValues[] resultValuesArray = new ContentValues[savedSearches.size()];
        for (int i = 0, j = savedSearches.size(); i < j; i++) {
            resultValuesArray[i] = createSavedSearch(savedSearches.get(i), accountId);
        }
        return resultValuesArray;
    }

    @NonNull
    public static ContentValues createStatus(final Status orig, final long accountId) {
        return ParcelableStatusValuesCreator.create(new ParcelableStatus(orig, accountId, false));
    }

    public static ContentValues createStatusDraft(final ParcelableStatusUpdate status) {
        return createStatusDraft(status, ParcelableAccount.getAccountIds(status.accounts));
    }

    @NonNull
    public static ContentValues createActivity(final Activity activity, final long accountId) {
        final ContentValues values;
        switch (activity.getAction()) {
            case REPLY: {
                values = createStatusActivity(activity.getTargetStatuses()[0]);
                break;
            }
            case MENTION: {
                values = createStatusActivity(activity.getTargetObjectStatuses()[0]);
                break;
            }
            default: {
                values = new ContentValues();
                break;
            }
        }
        ParcelableActivityValuesCreator.writeTo(new ParcelableActivity(activity, accountId, false), values);
        return values;
    }

    @NonNull
    public static ContentValues createStatusActivity(final Status orig) {
        if (orig == null) throw new NullPointerException();
        final ContentValues values = new ContentValues();
        final Status status;
        if (orig.isRetweet()) {
            final Status retweetedStatus = orig.getRetweetedStatus();
            final User retweetUser = orig.getUser();
            final long retweetedById = retweetUser.getId();
            values.put(Activities.STATUS_RETWEETED_BY_USER_ID, retweetedById);
            status = retweetedStatus;
        } else if (orig.isQuote()) {
            final Status quotedStatus = orig.getQuotedStatus();
            final User quoteUser = orig.getUser();
            final long quotedById = quoteUser.getId();
            final String textHtml = TwitterContentUtils.formatStatusText(orig);
            values.put(Activities.STATUS_QUOTE_TEXT_HTML, textHtml);
            values.put(Activities.STATUS_QUOTE_TEXT_PLAIN, TwitterContentUtils.unescapeTwitterStatusText(orig.getText()));
            values.put(Activities.STATUS_QUOTE_SOURCE, orig.getSource());
            values.put(Activities.STATUS_QUOTED_USER_ID, quotedById);
            status = quotedStatus;
        } else {
            status = orig;
        }
        final User user = status.getUser();
        final long userId = user.getId();
        values.put(Activities.STATUS_USER_ID, userId);
        final String textHtml = TwitterContentUtils.formatStatusText(status);
        values.put(Activities.STATUS_TEXT_HTML, textHtml);
        values.put(Activities.STATUS_TEXT_PLAIN, TwitterContentUtils.unescapeTwitterStatusText(status.getText()));
        values.put(Activities.STATUS_SOURCE, status.getSource());
        return values;
    }


    public static ContentValues createStatusDraft(final ParcelableStatusUpdate status,
                                                  final long[] accountIds) {
        final ContentValues values = new ContentValues();
        values.put(Drafts.ACTION_TYPE, Drafts.ACTION_UPDATE_STATUS);
        values.put(Drafts.TEXT, status.text);
        values.put(Drafts.ACCOUNT_IDS, TwidereArrayUtils.toString(accountIds, ',', false));
        values.put(Drafts.IN_REPLY_TO_STATUS_ID, status.in_reply_to_status_id);
        values.put(Drafts.LOCATION, ParcelableLocation.toString(status.location));
        values.put(Drafts.IS_POSSIBLY_SENSITIVE, status.is_possibly_sensitive);
        values.put(Drafts.TIMESTAMP, System.currentTimeMillis());
        if (status.media != null) {
            try {
                values.put(Drafts.MEDIA, LoganSquare.serialize(Arrays.asList(status.media), ParcelableMediaUpdate.class));
            } catch (IOException ignored) {
            }
        }
        return values;
    }

    public static ContentValues[] createTrends(final List<Trends> trendsList) {
        if (trendsList == null) return new ContentValues[0];
        final List<ContentValues> resultList = new ArrayList<>();
        for (final Trends trends : trendsList) {
//            final long timestamp = trends.getAsOf().getTime();
            for (final Trend trend : trends.getTrends()) {
                final ContentValues values = new ContentValues();
                values.put(CachedTrends.NAME, trend.getName());
                values.put(CachedTrends.TIMESTAMP, System.currentTimeMillis());
                resultList.add(values);
            }
        }
        return resultList.toArray(new ContentValues[resultList.size()]);
    }

    public static ContentValues makeCachedUserContentValues(final ParcelableUser user) {
        if (user == null) return null;
        final ContentValues values = new ContentValues();
        values.put(CachedUsers.USER_ID, user.id);
        values.put(CachedUsers.NAME, user.name);
        values.put(CachedUsers.SCREEN_NAME, user.screen_name);
        values.put(CachedUsers.PROFILE_IMAGE_URL, user.profile_image_url);
        values.put(CachedUsers.CREATED_AT, user.created_at);
        values.put(CachedUsers.IS_PROTECTED, user.is_protected);
        values.put(CachedUsers.IS_VERIFIED, user.is_verified);
        values.put(CachedUsers.LISTED_COUNT, user.listed_count);
        values.put(CachedUsers.FAVORITES_COUNT, user.favorites_count);
        values.put(CachedUsers.FOLLOWERS_COUNT, user.followers_count);
        values.put(CachedUsers.FRIENDS_COUNT, user.friends_count);
        values.put(CachedUsers.STATUSES_COUNT, user.statuses_count);
        values.put(CachedUsers.LOCATION, user.location);
        values.put(CachedUsers.DESCRIPTION_PLAIN, user.description_plain);
        values.put(CachedUsers.DESCRIPTION_HTML, user.description_html);
        values.put(CachedUsers.DESCRIPTION_EXPANDED, user.description_expanded);
        values.put(CachedUsers.URL, user.url);
        values.put(CachedUsers.URL_EXPANDED, user.url_expanded);
        values.put(CachedUsers.PROFILE_BANNER_URL, user.profile_banner_url);
        values.put(CachedUsers.IS_FOLLOWING, user.is_following);
        values.put(CachedUsers.BACKGROUND_COLOR, user.background_color);
        values.put(CachedUsers.LINK_COLOR, user.link_color);
        values.put(CachedUsers.TEXT_COLOR, user.text_color);
        return values;
    }
}
