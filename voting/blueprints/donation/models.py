from voting.database import Cursor
from datetime import datetime

# Charity model
class Charity:
    def __init__(self, charity_name, charity_description, charity_image, reg_num,
                 bank_acc, ird_num, create_by, theme_id, charity_id=None):
        self.charity_name = charity_name
        self.charity_description = charity_description
        self.charity_image = charity_image
        self.reg_num = reg_num
        self.bank_acc = bank_acc
        self.ird_num = ird_num
        self.create_by = create_by
        self.theme_id = theme_id
        self.charity_id = charity_id

def get_charity_by_id(charity_id):
    '''Retrieve charity information based on charity_id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT ch.*, u.username as created_by_username FROM charities ch, users u
            WHERE ch.create_by=u.user_id and charity_id = %s
        """, (charity_id,))
        charity = cursor.fetchone()
    return charity

def create_charity(charity):
    '''Create a new charity'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            INSERT INTO charities (charity_name, charity_description, charity_image, reg_num, 
            bank_acc, ird_num, create_by, theme_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """, (charity.charity_name,
              charity.charity_description,
              charity.charity_image,
              charity.reg_num,
              charity.bank_acc,
              charity.ird_num,
              charity.create_by,
              charity.theme_id,
        ))
        charity_id = cursor.lastrowid
    return charity_id

def update_charity(charity_id, charity):
    '''Update charity information based on charity_id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
        UPDATE charities
        SET charity_name = %s, charity_description = %s, charity_image = %s, reg_num = %s, bank_acc = %s, ird_num = %s
        WHERE charity_id = %s
    """, (
            charity.charity_name,
            charity.charity_description,
            charity.charity_image,
            charity.reg_num,
            charity.bank_acc,
            charity.ird_num,
            charity_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0

def update_charity_approved(charity_id, approved, approved_by):
    '''Update if the charity is approved or not'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
        UPDATE charities
        SET approved = %s, 
        approved_at = NOW(),
        approved_by = %s
        WHERE charity_id = %s
    """, (
            approved,
            approved_by,
            charity_id
        ))
        affected_rows = cursor.rowcount
    return affected_rows > 0

def get_all_charities():
    '''Retrieve all charities'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM charities
        """)
        charities = cursor.fetchall()
    return charities


def get_all_approved_charities():
    '''Retrieve all approved charities'''
    with Cursor() as cursor:
        cursor.execute(
            '''SELECT ch.*, u.username as created_by_username 
            FROM charities ch, users u WHERE ch.create_by = u.user_id 
            AND ch.approved = 1 order by ch.created_at desc'''
        )
        charities = cursor.fetchall()
    return charities

def get_charities_by_theme(theme_id):
    '''Retrieve all charities based on theme_id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM charities WHERE theme_id = %s ORDER BY created_at DESC
        """, (theme_id,))
        charities = cursor.fetchall()
    return charities

def get_approved_charities_by_theme(theme_id):
    '''Retrieve all approved charities based on theme_id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT * FROM charities WHERE theme_id = %s AND approved = 1 ORDER BY created_at DESC
            """, (theme_id,))
        charities = cursor.fetchall()
    return charities

# Donation model
class Donation:
    def __init__(self, amount, card_num, message, donated_by, charity_id, donation_id=None):
        self.amount = amount
        self.card_num = card_num
        self.message = message
        self.donated_by = donated_by
        self.charity_id = charity_id
        self.donation_id = donation_id

def create_donation(donation):
    '''Donate money to charity'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
        INSERT INTO donations (amount, card_num, message, donated_by, charity_id)
                       VALUES (%s, %s, %s, %s, %s)
                       """,(
            donation.amount,
            donation.card_num,
            donation.message,
            donation.donated_by,
            donation.charity_id
                       ))
        donation_id = cursor.lastrowid
        return donation_id

def get_donation_by_id(donation_id):
    '''Retrieve donation information based on donation_id'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
        SELECT d.*, u.username as created_by_username FROM donations d, users u
        WHERE d.donated_by=u.user_id and donation_id = %s
        """, (donation_id,))
        donation = cursor.fetchone()
    return donation

def get_all_my_donations(user_id):
    '''Retrieve all donations made by the current user'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT d.donation_id, d.amount, d.donated_at, c.charity_name, d.message 
            FROM donations d 
            JOIN charities c ON d.charity_id = c.charity_id 
            WHERE d.donated_by = %s 
            ORDER BY d.donated_at DESC
        """, (user_id,))
        donations = cursor.fetchall()
    return donations

def get_total_donations_for_charity(charity_id):
    '''Retrieve the total donations for the specified charity'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT SUM(amount) as total_donations
            FROM donations
            WHERE charity_id = %s
        """, (charity_id,))
        result = cursor.fetchone()
    return result['total_donations'] if result['total_donations'] else 0

def get_donations_by_charity(charity_id):
    '''Retrieve all donations made to the specified charity, including donor information'''
    with Cursor(dictionary=True) as cursor:
        cursor.execute("""
            SELECT d.donation_id, d.amount, d.message, d.donated_at, u.first_name as donor_firstname, u.last_name as donor_lastname
            FROM donations d
            JOIN users u ON d.donated_by = u.user_id
            WHERE d.charity_id = %s
            ORDER BY d.donated_at DESC
        """, (charity_id,))
        donations = cursor.fetchall()
    return donations


def get_total_donations_by_period(charity_id, period):
    """
    Retrieve the total donations grouped by the specified period (YEAR, MONTH, DAY).

    Parameters:
        charity_id (int): The ID of the charity.
        period (str): The period to group by, must be 'YEAR', 'MONTH', or 'DAY'.

    Returns:
        list[dict]: A list of dictionaries with 'period' and 'total' keys.
    """
    # Ensure 'period' is a valid grouping option
    if period not in ('YEAR', 'MONTH', 'DAY'):
        raise ValueError("Period must be 'YEAR', 'MONTH', or 'DAY'")

    # Get the current year and month for filtering
    current_year = datetime.now().year
    current_month = datetime.now().month

    # Adjust the query based on the period
    if period == 'YEAR':
        query = f"""
            SELECT YEAR(donated_at) as period, SUM(amount) as total
            FROM donations
            WHERE charity_id = %s
            GROUP BY YEAR(donated_at)
            ORDER BY period DESC
        """
        params = (charity_id,)

    elif period == 'MONTH':
        query = f"""
            SELECT MONTH(donated_at) as period, SUM(amount) as total
            FROM donations
            WHERE charity_id = %s AND YEAR(donated_at) = %s
            GROUP BY MONTH(donated_at)
            ORDER BY period DESC
        """
        params = (charity_id, current_year)

    elif period == 'DAY':
        query = f"""
            SELECT DAY(donated_at) as period, SUM(amount) as total
            FROM donations
            WHERE charity_id = %s AND YEAR(donated_at) = %s AND MONTH(donated_at) = %s
            GROUP BY DAY(donated_at)
            ORDER BY period DESC
        """
        params = (charity_id, current_year, current_month)

    with Cursor(dictionary=True) as cursor:
        cursor.execute(query, params)
        results = cursor.fetchall()

    return results


def search_donors(query):
    '''Search donors based on first or last name'''
    with Cursor(dictionary=True) as cursor:
        sql = '''
            SELECT d.donation_id, d.amount, d.donated_at, d.message, 
                   u.first_name as donor_firstname, u.last_name as donor_lastname
            FROM donations d
            JOIN users u ON d.donated_by = u.user_id
            WHERE u.first_name LIKE %s OR u.last_name LIKE %s
            ORDER BY d.donated_at DESC
        '''
        params = [f'%{query}%', f'%{query}%']
        cursor.execute(sql, params)
        donors = cursor.fetchall()
    return donors
