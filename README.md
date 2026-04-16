# Challenge Problem 5

This challenge is about preparing data for import into a SQL database.

The raw contest file in `files/2015.csv` contains repeated institution information across many rows. This program splits that data into two cleaner CSV files so the information is easier to load into a database:

- `files/Institutions.csv`
- `files/Teams.csv`

## What the program does

- Reads each row from `2015.csv`
- Assigns each distinct institution a numeric ID
- Writes one row per institution to `Institutions.csv`
- Writes one row per team to `Teams.csv`

The idea is that `Institutions.csv` can be used as the parent table and `Teams.csv` can use `Institution ID` as the link back to the institution record.

## How to run

You need Java installed on your machine first. If `java -version` and `javac -version` work in your terminal, you are good to go.

If you do not have Java installed:

- On macOS, the easiest option is usually Homebrew:
```bash
brew install openjdk
```
- On Windows or Linux, install a recent JDK from [Adoptium](https://adoptium.net/) or another JDK provider.

After installation, reopen your terminal and make sure `java -version` works.

To run the program, follow these steps:

1. Clone the repository:
```bash
git clone https://github.com/mollyoconnorr/ChallengeProblem5.git
```
2. Move into the folder:
```bash
cd ChallengeProblem5
```
3. Install Java if you do not already have it.
4. Run the program from the terminal:
```bash
javac Split2015.java
java Split2015
```
5. Use the generated CSV files for your database import.

That will read `files/2015.csv`, create `files/Institutions.csv` and `files/Teams.csv`, and prepare the data in a form that is easier to import into SQL.

## Notes

- The program expects the `files` folder to be next to `Split2015.java`.
- The source CSV includes some quoted institution names, so the code includes a basic CSV parser.
- If you move the Java file or the `files` folder, the relative paths will need to be updated.
