import os
import re

docs_dir = r"d:\InstaGallery\instagallery-backend\docs"
keywords = [
    r"\btai khoan\b",
    r"\bmat khau\b",
    r"\bdang nhap\b",
    r"\bchuc nang\b",
    r"\bnguoi dung\b",
    r"\bquan ly\b",
    r"\bchi tiet\b",
    r"\bhe thong\b",
    r"\bthong bao\b",
    r"\bthiet bi\b",
    r"\bdich vu\b",
    r"\bdanh gia\b",
    r"\bghi chu\b",
    r"\bhien co\b",
    r"\bnguy hiem\b",
    r"\bnhan xet\b",
    r"\btong quan\b",
    r"\bchua\b",
    r"\bhieu qua\b",
    r"\bchuan hoa\b",
    r"\byeu cau\b",
    r"\bho tro\b"
]

results = []

for root, dirs, files in os.walk(docs_dir):
    for file in files:
        if file.endswith('.md'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
            except Exception as e:
                continue
            
            matches = {}
            for kw in keywords:
                matches_found = re.findall(kw, content, re.IGNORECASE)
                if matches_found:
                    matches[kw] = len(matches_found)
            
            if matches:
                results.append((filepath, matches))

for filepath, matches in results:
    print(f"File: {filepath}")
    for kw, count in matches.items():
        print(f"  {kw}: {count}")
