import streamlit as ui
import pdfplumber as pdf_reader
import spacy as sp
import re as regex
from sklearn.feature_extraction.text import TfidfVectorizer as TFIDF
from sklearn.metrics.pairwise import cosine_similarity as cos_sim

nlp_model = sp.load("en_core_web_sm")

SKILLS_DB = [
    "python", "java", "c++", "c", "sql", "mysql", "postgresql", "mongodb",
    "machine learning", "deep learning", "artificial intelligence", "nlp",
    "natural language processing", "computer vision", "data science",
    "pandas", "numpy", "matplotlib", "seaborn", "scikit-learn",
    "tensorflow", "pytorch", "keras", "streamlit", "flask", "django",
    "html", "css", "javascript", "react", "github", "git", "docker",
    "linux", "aws", "azure", "gcp", "api", "rest api", "communication",
    "problem solving", "leadership", "teamwork", "excel"
]

def read_pdf(file):
    content = []
    with pdf_reader.open(file) as pdf:
        for p in pdf.pages:
            content.append(p.extract_text() or "")
    return "\n".join(content)

def compute_score(text1, text2):
    vec = TFIDF(stop_words="english")
    matrix = vec.fit_transform([text1, text2])
    return round(cos_sim(matrix[0:1], matrix[1:2])[0][0] * 100, 2)

def find_skills(text, skill_db):
    lower_text = text.lower()
    detected = []
    for skill in skill_db:
        if regex.search(r"\b" + regex.escape(skill) + r"\b", lower_text):
            detected.append(skill)
    return sorted(set(detected))

def check_sections(text):
    txt = text.lower()
    return {
        "skills": any(x in txt for x in ["skills", "technical skills"]),
        "experience": any(x in txt for x in ["experience", "work experience", "professional experience"]),
        "projects": any(x in txt for x in ["projects", "project", "academic projects"]),
        "education": "education" in txt,
        "certifications": "certifications" in txt
    }

def generate_feedback(sec, text):
    notes = []
    notes.append("Skills section found." if sec["skills"] else "Add a clear Skills section.")
    notes.append("Experience section found." if sec["experience"] else "Add an Experience section with internships, jobs, or training.")
    notes.append("Projects section found." if sec["projects"] else "Add 1-2 relevant projects to strengthen your profile.")
    notes.append("Education section found." if sec["education"] else "Add an Education section.")
    notes.append("Certifications section found." if sec["certifications"] else "Add certifications if you have them.")
    if regex.search(r"\b\d+%|\b\d+\b", text):
        notes.append("Good: Resume contains measurable details.")
    else:
        notes.append("Add measurable impact like 'improved accuracy by 20%' or 'reduced time by 30%'.")
    return notes

def suggest_improvements(missing, sec, jd):
    tips = []
    jd_text = jd.lower()
    if missing:
        tips.append(f"Learn or mention missing skills: {', '.join(missing[:5])}")
    if ("nlp" in jd_text or "natural language processing" in jd_text) and ("nlp" not in missing):
        tips.append("Add projects related to NLP.")
    if ("machine learning" in jd_text or "ml" in jd_text) and ("machine learning" not in missing):
        tips.append("Show ML projects with models, datasets, and results.")
    if not sec["projects"]:
        tips.append("Include a projects section with 1–3 relevant projects.")
    if not regex.search(r"\b\d+%|\b\d+\b", jd):
        tips.append("Use measurable impact in bullet points, such as percentages or counts.")
    tips.append("Use strong action verbs like built, developed, improved, optimized, and deployed.")
    return tips

ui.set_page_config(page_title="Resume Analyzer", layout="centered")

ui.title("Resume Analyzer Tool")
ui.write("Upload a resume and compare it with a job description")

file_input = ui.file_uploader("Upload Resume (PDF)", type=["pdf"])
jd_input = ui.text_area("Enter Job Description", height=220)

if ui.button("Analyze Resume"):
    if file_input is None or jd_input.strip() == "":
        ui.warning("Please upload a resume and provide a job description.")
    else:
        with ui.spinner("Processing..."):
            resume_text = read_pdf(file_input)
            if not resume_text.strip():
                ui.error("Unable to read the PDF content.")
            else:
                match_score = compute_score(resume_text, jd_input)
                resume_skill_set = find_skills(resume_text, SKILLS_DB)
                jd_skill_set = find_skills(jd_input, SKILLS_DB)
                common = sorted(set(resume_skill_set) & set(jd_skill_set))
                missing = sorted(set(jd_skill_set) - set(resume_skill_set))
                section_data = check_sections(resume_text)
                feedback_data = generate_feedback(section_data, resume_text)
                suggestions_data = suggest_improvements(missing, section_data, jd_input)

        ui.success("Analysis Complete!")

        ui.subheader("Match Score")
        ui.metric("Resume Match %", f"{match_score}%")

        colA, colB = ui.columns(2)

        with colA:
            ui.subheader("Matched Skills")
            if common:
                for s in common:
                    ui.write(f"- {s}")
            else:
                ui.write("No strong matches")

        with colB:
            ui.subheader("Missing Skills")
            if missing:
                for s in missing:
                    ui.write(f"- {s}")
            else:
                ui.write("No missing skills")

        ui.subheader("Section Feedback")
        for f in feedback_data:
            ui.write(f"- {f}")

        ui.subheader("Suggestions")
        for s in suggestions_data:
            ui.write(f"- {s}")