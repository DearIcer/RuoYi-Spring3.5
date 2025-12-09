# Flat Table View

<cite>
**Referenced Files in This Document**   
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm)
- [VelocityUtils.java](file://src/main/java/com/ruoyi/project/tool/gen/util/VelocityUtils.java)
- [GenUtils.java](file://src/main/java/com/ruoyi/project/tool/gen/util/GenUtils.java)
- [api.js.vm](file://src/main/resources/vm/js/api.js.vm)
- [TableDataInfo.java](file://src/main/java/com/ruoyi/framework/web/page/TableDataInfo.java)
- [BaseController.java](file://src/main/java/com/ruoyi/framework/web/controller/BaseController.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Template Structure and Components](#template-structure-and-components)
3. [Velocity Placeholders and Dynamic Content](#velocity-placeholders-and-dynamic-content)
4. [Search and Filtering Implementation](#search-and-filtering-implementation)
5. [Form Generation and Validation](#form-generation-and-validation)
6. [Table Rendering and Column Types](#table-rendering-and-column-types)
7. [CRUD Operations and API Integration](#crud-operations-and-api-integration)
8. [Permission Control and Button Management](#permission-control-and-button-management)
9. [Pagination and Data Handling](#pagination-and-data-handling)
10. [Sub-table Support](#sub-table-support)
11. [Best Practices and Customization](#best-practices-and-customization)

## Introduction

The index.vue.vm Velocity template is a core component of the RuoYi-Vue code generation system, designed to automatically create flat table-based Vue components for CRUD (Create, Read, Update, Delete) operations. This template leverages Velocity's templating capabilities to dynamically generate Vue components that integrate with Element UI components such as el-table, el-form, el-dialog, and el-pagination. The generated components provide a standardized interface for managing data entities, with automatic support for search functionality, form validation, inline editing, batch operations, and permission-controlled buttons.

The template serves as a foundation for rapid development in the RuoYi-Vue framework, allowing developers to quickly generate consistent and functional UI components for database tables. By using Velocity placeholders and conditional logic, the template adapts to different business entities, column types, and requirements, producing tailored Vue components that maintain a consistent user experience across the application.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L1-L603)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L1-L591)

## Template Structure and Components

The index.vue.vm template follows a structured layout that organizes the user interface into distinct sections: search criteria, action buttons, data table, pagination, and form dialogs. The template uses Vue's single-file component structure with three main parts: template, script, and style (though style is not explicitly defined in the template).

The primary components used in the template include:
- **el-form**: For search criteria and data entry forms
- **el-table**: For displaying data in a tabular format
- **el-pagination**: For navigating through paginated results
- **el-dialog**: For displaying forms in modal dialogs
- **el-button**: For action buttons with various operations
- **right-toolbar**: A custom component for toggling search visibility

The template also incorporates specialized components like image-preview, image-upload, file-upload, and editor for handling specific data types. These components are conditionally rendered based on the column configuration, allowing the template to support various input types including text, numbers, dates, files, and rich text.

The layout is responsive and follows Element UI's grid system with el-row and el-col components to organize buttons and form elements. The template uses Vue's v-show directive to control the visibility of the search section, allowing users to toggle it as needed.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L1-L181)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L1-L164)

## Velocity Placeholders and Dynamic Content

The index.vue.vm template extensively uses Velocity placeholders to dynamically populate component content based on the business entity being generated. These placeholders are replaced with actual values during the code generation process, creating customized Vue components for each entity.

Key Velocity placeholders include:
- **$!{className}**: Represents the class name of the business entity, used for component naming and API imports
- **$!{moduleName}**: Represents the module name, used for API endpoint routing and file organization
- **$!{businessName}**: Represents the business name, used for variable naming and API endpoint construction
- **$!{functionName}**: Represents the function name, used for display labels and dialog titles
- **$!{permissionPrefix}**: Represents the permission prefix, used for access control directives
- **$!{fields}**: Represents the collection of fields/columns, used for iterating through entity properties

The template uses Velocity's #foreach directive to iterate through columns and generate appropriate UI elements for each field. Conditional logic with #if directives determines the type of input control to render based on the column's htmlType, dictType, and other properties. For example, a column with htmlType "select" and a defined dictType will generate a dropdown populated with dictionary values, while a column with htmlType "datetime" will generate a date picker.

The template also uses Velocity's string manipulation functions like substring, toUpperCase, and toLowerCase to transform variable names and create consistent naming patterns. This allows the template to generate camelCase variable names from database field names and create proper labels from column comments.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L4-L603)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L4-L591)
- [VelocityUtils.java](file://src/main/java/com/ruoyi/project/tool/gen/util/VelocityUtils.java#L37-L73)

## Search and Filtering Implementation

The search functionality in the generated components is implemented through a combination of form elements and event handlers that filter data based on user input. The template generates a search form with input fields for each column marked as queryable in the entity configuration.

For different column types, the template creates appropriate input controls:
- **String fields**: Generate el-input components with placeholder text and clearable functionality
- **Select/radio fields with dictionary types**: Generate el-select components populated with dictionary values
- **Date fields**: Generate el-date-picker components with appropriate value formatting
- **Date range fields**: Generate el-date-picker components with type "daterange" for filtering between dates

The search form includes "Search" and "Reset" buttons that trigger the handleQuery and resetQuery methods respectively. The handleQuery method sets the page number to 1 and calls the getList method to refresh the data with the current search criteria. The resetQuery method clears all search fields and resets the date ranges, then calls handleQuery to reload the data.

The template uses Vue's v-model directive to bind form fields to the queryParams object, ensuring that user input is automatically synchronized with the component's data. For date range queries, the template creates separate variables (daterange${AttrName}) to store the date range values, which are then converted to begin and end parameters in the API request.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L3-L67)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L3-L66)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L477-L492)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L454-L470)

## Form Generation and Validation

The form generation in the index.vue.vm template creates both search forms and data entry forms for creating and editing records. The template uses the el-form and el-form-item components to structure the forms, with appropriate input controls based on the field type.

For data entry forms, the template generates an el-dialog containing an el-form with form items for each non-primary key field marked for insertion. The form includes validation rules defined in the rules object, which are automatically generated based on the column configuration. Required fields are marked with validation rules that display appropriate error messages when empty.

The template supports various input types:
- **Text input**: el-input for string fields
- **Textarea**: el-input with type="textarea" for longer text content
- **Select**: el-select for dropdowns, populated with dictionary values when available
- **Radio**: el-radio-group for single selection from options
- **Checkbox**: el-checkbox-group for multiple selection
- **Date picker**: el-date-picker for date fields
- **Image upload**: image-upload component for image files
- **File upload**: file-upload component for general files
- **Rich text editor**: editor component for formatted text

Form validation is implemented using Element UI's built-in validation system. The rules object contains validation rules for each field, with required fields having a "required" rule that triggers based on the input type (blur for text inputs, change for selects and radios). The submitForm method validates the entire form before submitting, preventing submission if validation fails.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L184-L351)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L167-L341)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L411-L425)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L387-L401)

## Table Rendering and Column Types

The table rendering in the generated components uses Element UI's el-table component to display data in a tabular format. The template creates columns for each field marked for display in the entity configuration, with appropriate formatting based on the data type.

Key column types and their rendering:
- **Primary key fields**: Displayed as regular columns with the field value
- **Date/datetime fields**: Formatted using the parseTime filter to display in a user-friendly format (e.g., "2023-12-01")
- **Image fields**: Rendered using the image-preview component to display thumbnails
- **Dictionary fields**: Displayed using the dict-tag component, which converts dictionary values to their corresponding labels
- **Checkbox fields**: Rendered as comma-separated values or using dict-tag for dictionary-backed checkboxes

The template uses Vue's slot-scope feature to customize the rendering of specific columns. For example, date fields use a template slot to format the date value, while image fields use a template slot to display the image preview. Dictionary fields use the dict-tag component to display the human-readable label instead of the raw value.

The table includes a selection column (el-table-column with type="selection") that allows users to select multiple rows for batch operations. It also includes an "Operations" column with buttons for editing and deleting individual records, which are conditionally displayed based on user permissions.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L116-L172)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L111-L155)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L128-L152)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L124-L147)

## CRUD Operations and API Integration

The generated components implement full CRUD functionality through integration with the RuoYi-Vue API service layer. The template imports API functions from a dynamically generated API module based on the entity's module and business name.

The API integration follows a consistent pattern:
- **List retrieval**: The getList method calls list${BusinessName}(queryParams) to fetch paginated data
- **Record retrieval**: The handleUpdate method calls get${BusinessName}(id) to fetch a specific record for editing
- **Record creation**: The submitForm method calls add${BusinessName}(form) to create a new record
- **Record update**: The submitForm method calls update${BusinessName}(form) to update an existing record
- **Record deletion**: The handleDelete method calls del${BusinessName}(id) to delete a record

The API responses follow the TableDataInfo structure, which includes a "rows" property containing the data array and a "total" property with the total count for pagination. The component maps these properties to ${businessName}List and total respectively.

For form submission, the template handles special data types:
- **Checkbox fields**: Values are joined with commas before submission and split when loading for editing
- **Date range queries**: Values are converted to begin${AttrName} and end${AttrName} parameters
- **Sub-table data**: Child records are included in the form data when present

Error handling is implemented using the $modal message system, which displays success or error messages to the user after API operations.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L356-L356)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L346-L346)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L433-L455)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L406-L429)
- [api.js.vm](file://src/main/resources/vm/js/api.js.vm#L1-L45)
- [TableDataInfo.java](file://src/main/java/com/ruoyi/framework/web/page/TableDataInfo.java#L1-L85)

## Permission Control and Button Management

The generated components implement fine-grained permission control using the v-hasPermi directive, which determines whether a user can see and interact with specific buttons. This directive checks the user's permissions against the required permission string before rendering the element.

Permission-controlled buttons include:
- **Add button**: Visible when user has '${permissionPrefix}:add' permission
- **Edit button**: Visible when user has '${permissionPrefix}:edit' permission
- **Delete button**: Visible when user has '${permissionPrefix}:remove' permission
- **Export button**: Visible when user has '${permissionPrefix}:export' permission

The template also implements state-based button disabling:
- **Edit button**: Disabled when no row or multiple rows are selected (single = false)
- **Delete button**: Disabled when no rows are selected (multiple = true)

The permission system is integrated with RuoYi's security framework, where permissions are defined in the database and assigned to roles. The permissionPrefix is generated as '${moduleName}:${businessName}', creating a consistent naming convention for permissions across the application.

The template also includes a right-toolbar component that allows users to toggle the search section visibility, improving the user experience by providing a clean interface when search is not needed.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L78-L112)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L76-L107)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L495-L497)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L474-L476)
- [sql.vm](file://src/main/resources/vm/sql/sql.vm#L8-L15)

## Pagination and Data Handling

The pagination system in the generated components uses Element UI's pagination component integrated with RuoYi's TableDataInfo response structure. The template implements server-side pagination by including pageNum and pageSize parameters in the API requests.

Key pagination features:
- **Page size control**: Users can select the number of records per page (default 10)
- **Page navigation**: Users can navigate between pages using the pagination controls
- **Total count display**: Shows the total number of records matching the search criteria
- **Loading state**: Displays a loading indicator while data is being fetched

The template initializes the queryParams object with default values for pageNum (1) and pageSize (10), which are automatically included in API requests. When the user changes the page or page size, the pagination component emits a "pagination" event that triggers the getList method with the updated parameters.

The component handles the API response by extracting the "rows" and "total" properties from the TableDataInfo object and assigning them to ${businessName}List and total respectively. This allows the table and pagination components to display the correct data and navigation controls.

For better user experience, the template includes a v-loading directive on the el-table to show a loading spinner while data is being fetched, preventing users from interacting with incomplete data.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L174-L180)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L157-L163)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L399-L402)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L379-L384)
- [TableDataInfo.java](file://src/main/java/com/ruoyi/framework/web/page/TableDataInfo.java#L1-L85)
- [TableSupport.java](file://src/main/java/com/ruoyi/framework/web/page/TableSupport.java#L1-L56)

## Sub-table Support

The index.vue.vm template includes support for master-detail relationships through sub-table functionality. When a business entity has a sub-table relationship, the template generates additional UI elements for managing the child records.

Key sub-table features:
- **Sub-table display**: An additional el-table is rendered below the main form to display child records
- **Sub-table operations**: Buttons for adding and deleting child records
- **Inline editing**: Child records can be edited directly in the table cells
- **Data binding**: Child records are included in the form data when submitting

The template generates specific methods for sub-table operations:
- **handleAdd${subClassName}**: Adds a new empty row to the sub-table
- **handleDelete${subClassName}**: Deletes selected rows from the sub-table
- **row${subClassName}Index**: Sets the index for each row in the sub-table
- **handle${subClassName}SelectionChange**: Tracks selected rows in the sub-table

The sub-table supports various input types for its columns, including text inputs, date pickers, and select dropdowns populated with dictionary values. The template ensures that child records are properly initialized and included in the form data when creating or updating the parent record.

This feature enables the creation of complex forms with hierarchical data structures, allowing users to manage related entities within a single interface.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L287-L345)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L270-L333)
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L561-L593)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L545-L581)

## Best Practices and Customization

The index.vue.vm template follows several best practices for Vue component development and code generation:

1. **Consistent naming conventions**: Uses camelCase for variables and PascalCase for component names
2. **Separation of concerns**: Keeps template, script, and style concerns separate
3. **Reactive data management**: Uses Vue's reactivity system to automatically update the UI
4. **Error handling**: Implements proper error handling for API calls
5. **Accessibility**: Includes appropriate labels and ARIA attributes
6. **Performance optimization**: Uses efficient data binding and event handling

For customization, developers can extend the template by:
- **Adding new field types**: Creating new Velocity conditions for custom input controls
- **Enhancing validation**: Adding custom validation rules and messages
- **Customizing layouts**: Modifying the form and table layouts for specific use cases
- **Adding business logic**: Extending the methods with additional functionality
- **Integrating with other components**: Adding support for third-party Vue components

When customizing the template, it's important to maintain consistency with the existing code structure and follow the established patterns for API integration, permission control, and data handling. This ensures that custom components remain compatible with the rest of the RuoYi-Vue framework and can be easily maintained and updated.

**Section sources**
- [index.vue.vm](file://src/main/resources/vm/vue/index.vue.vm#L1-L603)
- [v3/index.vue.vm](file://src/main/resources/vm/vue/v3/index.vue.vm#L1-L591)
- [VelocityUtils.java](file://src/main/java/com/ruoyi/project/tool/gen/util/VelocityUtils.java#L37-L73)
- [GenUtils.java](file://src/main/java/com/ruoyi/project/tool/gen/util/GenUtils.java#L1-L258)