I want you to create a Java program who will generate code.
Create a simple GUI for the program where i can input 
    - the table scheme (only Postgres for now)
    - the file location (where to put the generated code)

The code that i want to generate is a full REST API Laravel CRUD for a given table.
I use Laravel 10.

Here is the file that you need to generate: 
- a model
- a controller
    . handles only HTTP request to keep it thin and clean
    . handle request validation 
- a service class
    . handle any business logic related to the model
- a text file with the necessary routes 

The goal is that i just input the table scheme, and then the program generate the required code for a REST API CRUD functionality.

Try to write a clean and maintaible code for the Java program (use OOP principles).
For the generated code, use the best practice for a REST API CRUD functionality while maintaining the cleanest minimum viable code (that why i want you to create only Controller, Model, and Service). Iwant something functional but simple and clean ...

/*----------------------------------------------------------------------- */

Don't re-write the full code of the class or the file but just give the modified part tell me exactly where to modify. If you are adding new lines of code, just give me the new lines and tell me where to paste it.

/*----------------------------------------------------------------------- */

Is this implementation of a Laravel REST API CRUD generator take into account the FK relationship ?