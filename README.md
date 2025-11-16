# QA Maturity Assessment GitHub Pages

This repository contains a GitHub Pages site for the QA Maturity Assessment & 24-Month Roadmap.

## 🚀 Quick Start - Deploy to GitHub Pages

### Step 1: Create a GitHub Repository

1. Go to [GitHub](https://github.com) and log in
2. Click the **+** icon in the top right and select **New repository**
3. Name your repository (e.g., `qa-maturity-assessment`)
4. Choose **Public** (required for free GitHub Pages)
5. Click **Create repository**

### Step 2: Push Your Code

Open Terminal in this folder and run these commands:

```bash
# Initialize git repository
git init

# Add all files
git add .

# Commit the files
git commit -m "Initial commit - QA Maturity Assessment site"

# Add your GitHub repository as remote
git remote add origin https://github.com/manistiw/github-Pages.git

# Push to GitHub
git branch -M main
git push -u origin main
```

### Step 3: Enable GitHub Pages

1. Go to your repository on GitHub
2. Click on **Settings** tab
3. Scroll down to **Pages** section in the left sidebar
4. Under **Source**, select **Deploy from a branch**
5. Under **Branch**, select **main** and **/ (root)**
6. Click **Save**

### Step 4: Access Your Site

Your site is now live at:
```
https://manistiw.github.io/github-Pages/
```

It may take a few minutes for the site to be published. You'll see a green checkmark when it's ready!

## 📝 Making Updates

After making changes to `index.html`:

```bash
git add .
git commit -m "Updated content"
git push
```

GitHub Pages will automatically update your site within a few minutes.

## 🎨 Customization

You can customize the site by editing `index.html`:
- Change colors in the `<style>` section
- Add new sections
- Update content

## 📧 Support

For issues or questions, refer to [GitHub Pages Documentation](https://docs.github.com/en/pages)
