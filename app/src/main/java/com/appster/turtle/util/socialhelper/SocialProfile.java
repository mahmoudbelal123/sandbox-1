/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.socialhelper;

import java.io.Serializable;

//import org.brickred.socialauth.util.BirthDate;

/**
 * Data bean for profile information.
 *
 * @author tarunn@brickred.com
 */
public class SocialProfile implements Serializable {
    private static final long serialVersionUID = 6082073969740796991L;
    /**
     * Email
     */
    private String email;

    /**
     * Social Type
     */
    private String socialType;

    /**
     * First Name
     */
    private String firstName;

    /**
     * Last Name
     */
    private String lastName;

    /**
     * profile image URL
     */
    private String profileImageURL;

    /**
     * provider id with this profile associates
     */
    private String providerId;

    private String sourceType;
    private String gender;

    /**
     * Retrieves the first name
     *
     * @return String the first name
     */

    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the first name
     *
     * @param firstName the first name of User
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retrieves the last name
     *
     * @return String the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the last name
     *
     * @param lastName the last name of User
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email address.
     *
     * @return email address of the User
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the email
     *
     * @param email the email of User
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Retrieves the profile image URL
     *
     * @return String the profileImageURL
     */
    public String getProfileImageURL() {
        return profileImageURL;
    }

    /**
     * Updates the profile image URL
     *
     * @param profileImageURL profile image URL of User
     */
    public void setProfileImageURL(final String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    /**
     * Retrieves the provider id with this profile associates
     *
     * @return the provider id
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * Updates the provider id
     *
     * @param providerId the provider id
     */
    public void setProviderId(final String providerId) {
        this.providerId = providerId;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getDisplayName() {
        return firstName + (lastName != null && lastName.trim().length() > 0 ? " " + lastName : "");
    }

    public void setDisplayName(String displayName) {
        if (displayName != null && displayName.length() > 1) {
            String[] names = displayName.split(" ");
            firstName = names[0];
            if (names.length > 1)
                lastName = names[1];
        }
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}