
-- Sample Data / Pre loaded Data
INSERT INTO bister.business_partner (name, description, yali_key) VALUES('Olympia Group','','OLY');
INSERT INTO bister.address (name, address_line_1, address_line_2, landmark, city, state, country,postcode, latitude, longitude, address_type) VALUES('Olympia Group', 'Olympia Technology Park', 'Plot No.1, SIDCO Industrial Estate', NULL, 'Chennai', 'Tamilnadu', 'India','600100', '13.0153', '80.204', 'OFFICE');
INSERT INTO bister.organisation (name, description, yali_key, address_id, business_partner_id) VALUES('Olympia Group', 'Olympia Group', 'OLYORG', 1, 1);
INSERT INTO bister.address (name, address_line_1, address_line_2, landmark, city, state, country,postcode, latitude, longitude, address_type) VALUES('Olympia Technology Park', 'Plot No.1', 'SIDCO Industrial Estate', NULL, 'Chennai', 'Tamilnadu', 'India','602100', '', '', 'OFFICE');
INSERT INTO bister.yali_user (login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date) VALUES('chnguindyhead', '$2a$10$HgDN/mzEzCOBVBH0OGAdg.ksmoa/lPOd7NcEeqmB5wprRSTEA2OGK', 'Chennai guindy facility head', NULL, 'chnguindyhead@in.com', NULL, 1, 'en', NULL, 'AFHMfTq2OmgIxxN9lAZa', 'admin', '2022-10-16 20:47:17', '2022-10-16 20:47:17', 'admin', '2022-10-16 20:47:17');
INSERT INTO bister.facility (name, description,facility_type, address_id, user_id, organisation_id) VALUES('Olympia Group - Guindy', 'Olympia Group - Guindy', 'CONSULTING', 2, 1050, 1);
INSERT INTO bister.project_type (name, description) VALUES('Apartment', 'Apartment');

INSERT INTO bister.address (name, address_line_1, address_line_2, landmark, city, state, country,postcode, latitude, longitude, address_type) VALUES('DLF One Midtown', 'Karampura', '', NULL, 'Karampura', 'Delhi', 'India','603100', '', '', 'OFFICE');
INSERT INTO bister.project
(id, name, slug, description, short_description, regular_price, sale_price, published, date_created, date_modified, project_status, sharable_hash, estimated_budget, address_id, project_type_id)
VALUES(1, 'DLF One Midtown', 'DLF-One-Midtown', 'As an urban oasis in the heart of Delhi and a landmark address in the vibrant neighborhood of West Delhi', 'DLF One Midtown', 3.50, 350000.00, 1, '2022-10-16 21:09:00', '2022-10-17', 'NEW', '', 350000.00, 3, 1);
INSERT INTO bister.yali_user
(login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date)
VALUES('kumar', '$2a$10$q7cwa9OkWZH5YXtjROBO3utEwna2QZHACtU72lWPS.Z9UzVcBtzZi', 'kumar', 'raj', 'kumar.raj@in.com', NULL, 1, 'en', NULL, 'H5eJPUJ0Qu2P6CFJWW6S', 'admin', '2022-10-17 03:13:42', '2022-10-17 03:13:42', 'admin', '2022-10-17 03:13:42');
INSERT INTO bister.agent
(name, contact_number, avatar_url, agent_type, user_id, facility_id)
VALUES('kumar', '9122121213', NULL, 'SUPPORT', 1051, 1);
INSERT INTO bister.yali_user
(login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date)
VALUES('parthiban', '$2a$10$nI6whM.tDWupyfFsmVg9jOtdiPV6itk4frTzYD5UmWD937ZEk4U9q', 'parthiban', NULL, 'parthiban@in.com', NULL, 1, 'en', NULL, 'tXnrMZQI1n1WxNFJHT2H', 'admin', '2022-10-17 03:18:14', '2022-10-17 03:18:14', 'admin', '2022-10-17 03:18:14');
INSERT INTO bister.agent (name, contact_number , avatar_url, agent_type , user_id, facility_id) VALUES('parthiban', '923932922', NULL, 'LEAD', 1052, 1);
INSERT INTO bister.tag
( name, slug, description, tag_type, product_id, project_id)
VALUES('Apartment', 'Apartment', 'High raised Apartments', 'Project', NULL, 1);
INSERT INTO bister.project_specification_group
(title, slug, description, project_id)
VALUES('Doors', 'Doors', NULL, 1);

INSERT INTO bister.project_specification
(title, value, description, project_specification_group_id, project_id)
VALUES( 'Internal', 'Flush Shutters', NULL, 1, 1);
INSERT INTO bister.project_specification
( title, value, description, project_specification_group_id, project_id)
VALUES('Main', 'Wooden Frame', NULL, 1, 1);


INSERT INTO bister.attachment
(name, description, attachment_type, link, product_specification_id, project_specification_id)
VALUES('Internal door', 'Internal door', 'IMAGE', 'http://aaben.com/wp-content/uploads/2016/12/italian-doors-and-windows-25.jpg', NULL, 1);

INSERT INTO bister.attachment
(name, description, attachment_type, link, product_specification_id, project_specification_id)
VALUES('Internal door 2', 'Internal door 2', 'IMAGE', 'http://aaben.com/wp-content/uploads/2016/12/italian-doors-and-windows-57.jpg', NULL, 1);

INSERT INTO bister.phonenumber
(country, code, contact_number, phonenumber_type, user_id, organisation_id, facility_id)
VALUES('India', '+91', '9232392323', 'PERSONAL_PRIMARY', 1050, NULL, NULL);


INSERT INTO bister.yali_user
(id, login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date)
VALUES(1053, 'ambigai', '$2a$10$C5C7FdAcpNICZ5VLP2MwX.OjBSvBP2bLM8lAjRVgrNrLXdv36ng5q', 'ambigai', NULL, 'ambigai@in.com', NULL, 1, 'en', NULL, 'cnjVyMvz9BSJxh0NpVYT', 'admin', '2022-10-17 14:24:14', '2022-10-17 14:24:14', 'admin', '2022-10-17 14:24:14');

INSERT INTO bister.address (name, address_line_1, address_line_2, landmark, city, state, country,postcode, latitude, longitude, address_type) VALUES('Ambigai ', '3, SM Street', '', NULL, 'Mangaluru', 'Karnataga', 'India','103100', '', '', 'OFFICE');

INSERT INTO bister.project_review
(id, reviewer_name, reviewer_email, review, rating, status, reviewer_id, project_id)
VALUES(1, 'Kanna', 'kanna@in.com', 'It is good product and I would recommend to others.', 8, 'HOLD', 1, 1);


INSERT INTO bister.customer
(id, name, contact_number, avatar_url, user_id, address_id)
VALUES(1, 'Ambigai', '9232323232', NULL, 1053, 4);

INSERT INTO bister.enquiry
(id, subject , raised_date, details, last_response_date, last_response_id,  enquiry_type, status, agent_id, project_id, product_id, customer_id)
VALUES(1, 'Request for more information','2022-10-17 15:57:00', 'I would like to buy home in this project', '2022-10-17 15:57:00', NULL, 'PROJECT', 'OPEN', 1, 1, NULL, 1);

INSERT INTO bister.enquiry_response
(id, query, details, enquiry_response_type, enquiry_id)
VALUES(1, NULL, 'Thank for reaching us. Our representative will reach you in your place.', 'INPERSON', 1);

INSERT INTO bister.enquiry_response
(id, query, details, enquiry_response_type, enquiry_id)
VALUES(2, 'Customer accepted', 'We are closing as the customer is accepted the deal', 'CALL', 1);

INSERT INTO bister.project_activity
(id, title, details, status, project_id)
VALUES(1, 'We are open for Sale ', 'We have exited offers in the Golden period sale', 'LOG', 1);

INSERT INTO bister.project_activity
(id, title, details, status, project_id)
VALUES(2, 'The sales is in progress', 'Book your favourite Italian marble floored 2BHK homes', 'INPROGRESS', 1);

INSERT INTO bister.notification
(id, `date`, details, sent_date, google_notification_id, whatsapp_notification_id, sms_notification_id, product_id, project_id, schedule_id, promotion_id, yali_read, notification_source_type, notification_type, notification_mode, agent_id, user_id)
VALUES(1, '2022-10-18 03:51:00', 'Thank for reaching us. Our representative will reach you in your place.', '2022-10-18 03:51:00', 'XX12', NULL, NULL, NULL, 1, NULL, NULL, 0, 'ENQUIRY', 'DIGITAL', 'EMAIL', 1, 1053);

INSERT INTO bister.attachment
(id, name, description, attachment_type, link, is_approval_needed, approval_status, approved_by, product_id, project_id, enquiry_id, product_specification_id, project_specification_id)
VALUES(3, 'Olympia Group', NULL, 'IMAGE', 'http://aaben.com/wp-content/uploads/2016/12/italian-doors-and-windows-26.jpg', 0, 'NEW', NULL, NULL, 1, NULL, NULL, NULL);

INSERT INTO bister.promotion
(id, product_id, project_id, content_type, recipients, recipient_group, created_by, created_at, send_at, attachment_id)
VALUES(1, NULL, 1, 'ATTACHMENT', NULL, 'promotion_intrested_group_chennai', 2, '2022-10-18 04:35:00', '2022-10-20 04:35:00', 3);

INSERT INTO bister.certification
(id, name, slug, authority, status, project_id, prodcut, org_id, facitlity_id, created_by, created_at)
VALUES(1, 'CMDA approval', NULL, 'Chennai corrporation', 'PLANNED', 1, NULL, NULL, NULL, 1501, '2022-10-18 07:10:00');


INSERT INTO bister.category
(id, name, slug, description, category_type, parent_id)
VALUES(1, '2 BHK', '2bhk', NULL, 'Product', NULL);
INSERT INTO bister.category
(id, name, slug, description, category_type, parent_id)
VALUES(2, 'Apartment', 'Apartment', NULL, 'Project', NULL);

INSERT INTO bister.tax_class
(id, name, slug)
VALUES(1, 'Property tax Delhi', 'Property-tax-Delhi');

INSERT INTO bister.product
(id, name, slug, description, short_description, regular_price, sale_price, published, date_created, date_modified, featured, sale_status, sharable_hash, project_id, tax_class_id)
VALUES(1, '2 BHK', '2BHK ', '2Bed room 1Hall 1Kitchen  homes', '2Bed room 1Hall 1Kitchen  homes', 11700000.00, 11700000.00, 1, '2022-10-18 16:14:00', '2022-10-17', 0, 'OPEN', NULL, 1, 1);

INSERT INTO bister.product
(id, name, slug, description, short_description, regular_price, sale_price, published, date_created, date_modified, featured, sale_status, sharable_hash, project_id, tax_class_id)
VALUES(2, '3 BHK', '3BHK ', '3Bed room 1Hall 1Kitchen  homes', '3Bed room 1Hall 1Kitchen  homes', 14700000.00, 14700000.00, 1, '2022-10-18 16:14:00', '2022-10-17', 0, 'OPEN', NULL, 1, 1);

INSERT INTO bister.product_review
(id, reviewer_name, reviewer_email, review, rating, status, reviewer_id, product_id)
VALUES(1, 'karthi', 'karthi@gmail.com', 'It is good home and I would recommend to others.', 5, 'HOLD', 1501, 1);

INSERT INTO bister.product_attribute
(id, name, slug, `type`, notes, visible)
VALUES(1, 'Size', 'Size', 'Home', 'Add home size in sq ft', 1);

INSERT INTO bister.product_attribute
(id, name, slug, `type`, notes, visible)
VALUES(2, 'Builder Price', 'Builder Price', 'Builder Price', 'Add Builder Price in rupees', 1);

INSERT INTO bister.product_attribute_term
(id, name, slug, description, menu_order, product_attribute_id, product_id)
VALUES(1, '1700 sq ft', '2BHK 1700 sq ft', '1700 sq ft', 1, 1, 1);

INSERT INTO bister.product_attribute_term
(id, name, slug, description, menu_order, product_attribute_id, product_id)
VALUES(2, '350000000', '2BHK ', '3.5 Cr', 2, 2, 1);

INSERT INTO bister.product_attribute_term
(id, name, slug, description, menu_order, product_attribute_id, product_id)
VALUES(3, '2300 sq ft', '3BHK 2300 sq ft', '2300 sq ft', 1, 1, 2);

INSERT INTO bister.product_attribute_term
(id, name, slug, description, menu_order, product_attribute_id, product_id)
VALUES(4, '55000000', '3BHK  55000000', '5.5 Cr', 2, 2, 2);

INSERT INTO product_variation (asset_id, name, description, regular_price, sale_price, date_on_sale_from, date_on_sale_to, is_draft, use_parent_details, sale_status, product_id) 
VALUES ('1', 'First Floor 2BHK', 'First Floor 2BHK', 11700000, 11700000, '2022-10-18 16:14:00', '2022-10-18 16:14:00', true, true, 'OPEN', 1);

INSERT INTO bister.purchase_order
(id, placed_date, status, code, delivery_option, user_id, product_variation_id)
VALUES(1, '2022-10-21 16:33:00', 'NEW', '4e1c3986-515e-11ed-bdc3-0242ac120002', 'HAND_OVER', 1, 1);

INSERT INTO bister.yali_user
(id, login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date)
VALUES(1055, 'kumudha', '$2a$10$GBME0yLY8eh5P8DJcZI29Ok0LHQKiGxA3FtdBHB7GWIsSEpGeeHAq', NULL, NULL, 'kumudha.d@gmail.com', NULL, 1, 'en', 'U5W6d21X4YrWvfpNL2KH', NULL, 'system', '2022-11-02 16:34:03', NULL, 'system', '2022-11-02 16:34:03');

INSERT INTO bister.purchase_order
(id, placed_date, status, code, delivery_option, user_id, product_variation_id)
VALUES(2, '2022-11-03 17:19:00', 'NEW', '8696783234323423232332', 'HAND_OVER', 2, 1);
