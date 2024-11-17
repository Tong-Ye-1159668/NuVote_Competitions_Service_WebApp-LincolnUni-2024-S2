import mysql.connector
import random
from datetime import datetime, timedelta

from voting.database import Cursor


# Database connection


# Function to generate mock donations
def generate_donations(num_records):
    donations = []
    for donation_id in range(1, num_records + 1):
        charity_id = 2
        succeed = random.choice([0, 1])
        donated_by = random.randint(1, 110)

        # Generate a random datetime within the past two years
        donated_at = datetime.now() - timedelta(days=random.randint(1, 730))
        amount = round(random.uniform(50, 500), 2)  # Random amount between $50 and $500
        card_num = f"{random.randint(1000, 9999)} {random.randint(1000, 9999)} {random.randint(1000, 9999)} {random.randint(1000, 9999)}"
        message = random.choice([
            "Love you", "Happy to support!", "Thank you for your efforts!",
            "You’re making a difference!", "Together we can achieve more!"
        ])
        donations.append({
            'charity_id': charity_id,
            'succeed': succeed,
            'donated_by': donated_by,
            'donated_at': donated_at,
            'amount': amount,
            'card_num': card_num,
            'message': message
        })
    return donations


# Insert donations into the database
def insert_donations(donation):
    '''Donate money to charity'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
        INSERT INTO donations (charity_id, succeed, donated_by, donated_at, amount, card_num, message)
                       VALUES (%s, %s, %s, %s, %s, %s, %s)
                       """, (
            donation['charity_id'],
            donation['succeed'],
            donation['donated_by'],
            donation['donated_at'],
            donation['amount'],
            donation['card_num'],
            donation['message']))
        donation_id = cursor.lastrowid
        return donation_id


if __name__ == "__main__":
    # Generate and insert 100 donations
    donation_records = generate_donations(100)
    for d in donation_records:
        insert_donations(d)
